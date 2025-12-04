package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.entities.ClosedTransaction;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.mappers.ClosedTransactionMapper;
import com.webstore.usersMs.repositories.ClosedTransactionRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.repositories.OpenTransactionRepository;
import com.webstore.usersMs.services.ClosedTransactionService;
import com.webstore.usersMs.services.BillingPriceService;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.entities.OpenTransaction;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.error.WbException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClosedTransactionServiceImp implements ClosedTransactionService {

    private final ClosedTransactionRepository repository;
    private final CompanyRepository companyRepository;
    private final BillingPriceRepository billingPriceRepository;
    private final UserRepository userRepository;
    private final OpenTransactionRepository openTransactionRepository;
    private final BillingPriceService billingPriceService;
    private final UserService userService;

    private final ClosedTransactionMapper mapper = Mappers.getMapper(ClosedTransactionMapper.class);

    @Override
    public DClosedTransaction create(DClosedTransaction dto) throws WbException {
        ClosedTransaction entity = mapper.fromDto(dto);

        // Set relationships if IDs are provided
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(entity::setCompany);
        }

        if (dto.getBillingPriceBillingPriceId() != null) {
            Optional<BillingPrice> billingPrice = billingPriceRepository.findByBillingPriceId(dto.getBillingPriceBillingPriceId());
            billingPrice.ifPresent(entity::setBillingPrice);
        }

        if (dto.getSellerAppUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getSellerAppUserId());
            user.ifPresent(entity::setSellerAppUser);
        }

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DClosedTransaction update(DClosedTransaction dto) throws WbException {
        Optional<ClosedTransaction> entity = repository.findByClosedTransactionId(dto.getClosedTransactionId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }

        ClosedTransaction dbTransaction = entity.get();
        ClosedTransaction merged = mapper.merge(dto, dbTransaction);

        // Update relationships if IDs are provided
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(merged::setCompany);
        }

        if (dto.getBillingPriceBillingPriceId() != null) {
            Optional<BillingPrice> billingPrice = billingPriceRepository.findByBillingPriceId(dto.getBillingPriceBillingPriceId());
            billingPrice.ifPresent(merged::setBillingPrice);
        }

        if (dto.getSellerAppUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getSellerAppUserId());
            user.ifPresent(merged::setSellerAppUser);
        }

        return mapper.toDto(repository.save(merged));
    }

    @Override
    public Page<DClosedTransaction> findBy(String status, Long companyCompanyId, Pageable pageable) {
        Page<ClosedTransaction> page = repository.findBy(status, companyCompanyId, pageable);
        
        // Mapear las transacciones e incluir el currency del país
        return page.map(entity -> {
            DClosedTransaction dto = mapper.toDto(entity);
            // Si el mapper no mapeó el currency del país, agregarlo manualmente
            if (dto.getCountryCurrency() == null && entity.getCompany() != null && 
                entity.getCompany().getCountry() != null) {
                dto.setCountryCurrency(entity.getCompany().getCountry().getCurrency());
            }
            return dto;
        });
    }

    @Override
    public DClosedTransaction closeTransaction(Long openTransactionId) throws WbException {
        // Buscar la transacción abierta
        Optional<OpenTransaction> openTransactionOpt = openTransactionRepository.findByOpenTransactionId(openTransactionId);
        if (openTransactionOpt.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }

        OpenTransaction openTransaction = openTransactionOpt.get();
        
        // Verificar que la transacción esté en estado OPEN
        if (!"OPEN".equals(openTransaction.getStatus())) {
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.CAN_NOT_CREATE_USER);
        }

        // Obtener usuario autenticado
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Usuario no autenticado al intentar cerrar transacción: {}", openTransactionId);
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }
        
        if (authenticatedUser.getCompanyId() == null) {
            log.error("Usuario autenticado sin companyId. appUserId: {}", authenticatedUser.getAppUserId());
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        // Calcular tiempo transcurrido usando día y hora de ingreso
        LocalDateTime startDateTime = LocalDateTime.of(openTransaction.getStartDay(), openTransaction.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.now();
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        String timeElapsed = String.format("%d horas %d minutos", hours, minutes);

        // Calcular horas para la tarifa (redondeo hacia arriba)
        int hoursForBilling = (int) (hours + (minutes > 0 ? 1 : 0));
        if (hoursForBilling == 0) {
            hoursForBilling = 1; // Mínimo 1 hora
        }

        // Obtener tipo de vehículo como String
        String tipoVehiculoStr = null;
        if (openTransaction.getTipoVehiculo() != null) {
            tipoVehiculoStr = openTransaction.getTipoVehiculo().name();
        }

        // Validar que exista tipo de vehículo
        if (tipoVehiculoStr == null || tipoVehiculoStr.trim().isEmpty()) {
            log.error("Tipo de vehículo no encontrado en transacción abierta: {}", openTransactionId);
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_NOT_FOUND);
        }

        // Buscar tarifa según horas, tipo de vehículo y companyId
        // El servicio valida que el tiempo no exceda el rango máximo
        com.webstore.usersMs.dtos.DBillingPrice billingPriceDto = billingPriceService.calculatePriceByHours(
            hoursForBilling, 
            tipoVehiculoStr
        );
        
        // Actualizar transacción abierta a estado CLOSED
        openTransaction.setStatus("CLOSED");
        openTransaction.setEndTime(endDateTime.toLocalDate());
        openTransaction.setEndDate(endDateTime.toLocalTime());
        openTransaction.setTimeElapsed(timeElapsed);
        
        if (billingPriceDto != null) {
            Optional<com.webstore.usersMs.entities.BillingPrice> billingPrice = 
                billingPriceRepository.findByBillingPriceId(billingPriceDto.getBillingPriceId());
            if (billingPrice.isPresent()) {
                openTransaction.setBillingPrice(billingPrice.get());
                
                // Setear serviceTypeServiceTypeId desde el businessService del billingPrice
                if (billingPrice.get().getBusinessService() != null && 
                    billingPrice.get().getBusinessService().getBusinessServiceId() != null) {
                    openTransaction.setServiceTypeServiceTypeId(
                        billingPrice.get().getBusinessService().getBusinessServiceId());
                    log.info("serviceTypeServiceTypeId asignado desde billingPrice calculado: {}", 
                        billingPrice.get().getBusinessService().getBusinessServiceId());
                }
                
                // Calcular montos
                Long mount = billingPriceDto.getMount() != null ? billingPriceDto.getMount() : 0L;
                openTransaction.setAmount(mount.doubleValue());
                openTransaction.setTotalAmount(mount.doubleValue());
            }
        }

        // Asegurar que el currency esté asignado desde el país de la compañía si no está en openTransaction
        String currencyValue = openTransaction.getCurrency();
        if ((currencyValue == null || currencyValue.isEmpty()) && 
            openTransaction.getCompany() != null && 
            openTransaction.getCompany().getCountry() != null &&
            openTransaction.getCompany().getCountry().getCurrency() != null) {
            currencyValue = openTransaction.getCompany().getCountry().getCurrency();
            openTransaction.setCurrency(currencyValue);
            log.info("Currency asignado desde el país de la compañía al cerrar transacción: {}", currencyValue);
        }

        openTransactionRepository.save(openTransaction);

        // Obtener currency para closedTransaction (usar el de openTransaction o el del país de la compañía)
        String closedCurrency = currencyValue;
        if ((closedCurrency == null || closedCurrency.isEmpty()) && 
            openTransaction.getCompany() != null && 
            openTransaction.getCompany().getCountry() != null) {
            closedCurrency = openTransaction.getCompany().getCountry().getCurrency();
        }

        // Crear registro en closed_transaction
        ClosedTransaction closedTransaction = ClosedTransaction.builder()
            .startTime(openTransaction.getStartTime())
            .startDay(openTransaction.getStartDay())
            .endDate(endDateTime.toLocalTime())
            .endTime(endDateTime.toLocalDate())
            .currency(closedCurrency)
            .company(openTransaction.getCompany())
            .status("CLOSED")
            .billingPrice(openTransaction.getBillingPrice())
            .amount(openTransaction.getAmount())
            .discount(openTransaction.getDiscount())
            .totalAmount(openTransaction.getTotalAmount())
            .timeElapsed(timeElapsed)
            .operationDate(endDateTime)
            .serviceTypeServiceTypeId(openTransaction.getServiceTypeServiceTypeId())
            .sellerAppUser(openTransaction.getAppUserSeller())
            .sellerName(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName())
            .build();

        ClosedTransaction saved = repository.save(closedTransaction);
        log.info("Transacción cerrada: openTransactionId={}, closedTransactionId={}, horas={}", 
            openTransactionId, saved.getClosedTransactionId(), hoursForBilling);

        return mapper.toDto(saved);
    }

    @Override
    public com.webstore.usersMs.dtos.DClosedTransactionStats getTodayStats() throws WbException {
        // Obtener usuario autenticado
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Usuario no autenticado al intentar obtener estadísticas del día");
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }
        
        if (authenticatedUser.getCompanyId() == null) {
            log.error("Usuario autenticado sin companyId. appUserId: {}", authenticatedUser.getAppUserId());
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        Long companyId = authenticatedUser.getCompanyId();
        
        // Calcular inicio y fin del día una sola vez
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        
        // Ejecutar consultas en paralelo para mejor rendimiento
        Long totalTransactions = repository.countTodayTransactions(companyId);
        Double totalAmountDouble = repository.sumTodayAmount(companyId);
        java.util.List<ClosedTransaction> transactions = repository.findTodayTransactions(companyId, startOfDay, startOfNextDay);
        
        // Convertir totalAmount
        java.math.BigDecimal totalAmount = totalAmountDouble != null 
            ? java.math.BigDecimal.valueOf(totalAmountDouble) 
            : java.math.BigDecimal.ZERO;
        
        // Obtener currency de la primera transacción (ya viene con JOIN FETCH)
        // Si no hay transacciones, usar valor por defecto
        String currency = "USD"; // Valor por defecto
        if (!transactions.isEmpty()) {
            ClosedTransaction firstTransaction = transactions.get(0);
            if (firstTransaction.getCompany() != null && 
                firstTransaction.getCompany().getCountry() != null) {
                String countryCurrency = firstTransaction.getCompany().getCountry().getCurrency();
                if (countryCurrency != null && !countryCurrency.isEmpty()) {
                    currency = countryCurrency;
                }
            }
        }
        
        final String finalCurrency = currency; // Variable final para usar en el stream
        
        // Limitar a las últimas 50 transacciones para mejor rendimiento
        java.util.List<ClosedTransaction> limitedTransactions = transactions.stream()
            .limit(50)
            .collect(java.util.stream.Collectors.toList());
        
        // Mapear a DTOs
        java.util.List<com.webstore.usersMs.dtos.DClosedTransactionSummary> summaries = limitedTransactions.stream()
            .map(ct -> com.webstore.usersMs.dtos.DClosedTransactionSummary.builder()
                .closedTransactionId(ct.getClosedTransactionId())
                .operationDate(ct.getOperationDate())
                .timeElapsed(ct.getTimeElapsed())
                .totalAmount(ct.getTotalAmount() != null ? java.math.BigDecimal.valueOf(ct.getTotalAmount()) : java.math.BigDecimal.ZERO)
                .currency(finalCurrency)
                .sellerName(ct.getSellerName())
                .build())
            .collect(java.util.stream.Collectors.toList());
        
        return com.webstore.usersMs.dtos.DClosedTransactionStats.builder()
            .totalTransactions(totalTransactions)
            .totalAmount(totalAmount)
            .currency(currency)
            .transactions(summaries)
            .build();
    }
}

