package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.OPEN_TRANSACTION_NOT_FOUND;

import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.entities.enums.EtransactionStatus;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

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

        Optional<BillingPrice> billingPrice = billingPriceRepository
                .findByBillingPriceId(dto.getBillingPriceBillingPriceId());
        billingPrice.ifPresent(entity::setBillingPrice);

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

        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(merged::setCompany);
        }

        if (dto.getBillingPriceBillingPriceId() != null) {
            Optional<BillingPrice> billingPrice = billingPriceRepository
                    .findByBillingPriceId(dto.getBillingPriceBillingPriceId());
            billingPrice.ifPresent(merged::setBillingPrice);
        }

        if (dto.getSellerAppUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getSellerAppUserId());
            user.ifPresent(merged::setSellerAppUser);
        }

        return mapper.toDto(repository.save(merged));
    }

    @Override
    public Page<DClosedTransaction> findBy(EtransactionStatus status, Long companyCompanyId, LocalDateTime operationDateFrom,
                                           LocalDateTime operationDateTo, Pageable pageable) {
        // Construir la especificación dinámicamente para evitar problemas con nulls
        Specification<ClosedTransaction> spec = (root, query, cb) -> {
            if (query != null && (query.getResultType() == null ||
                    (!query.getResultType().equals(Long.class) && !query.getResultType().equals(long.class)))) {
                query.distinct(true);
                root.fetch("company").fetch("country");
            }

            Predicate predicate = cb.conjunction();

            if (status != null) {
                predicate = cb.and(predicate, cb.like(cb.upper(root.get("status")), "%" + status + "%"));
            }

            if (companyCompanyId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("company").get("companyId"), companyCompanyId));
            }

            if (operationDateFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("operationDate"), operationDateFrom));
            }

            if (operationDateTo != null) {
                predicate = cb.and(predicate, cb.lessThan(root.get("operationDate"), operationDateTo));
            }

            return predicate;
        };

        Page<ClosedTransaction> page = repository.findAll(spec, pageable);

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


        // Buscar la transacción abierta
        Optional<OpenTransaction> openTransactionOpt = openTransactionRepository
                .findByOpenTransactionIdAndCompanyCompanyId(openTransactionId, authenticatedUser.getCompanyId());

        if (openTransactionOpt.isEmpty()) {
            throw new WbException(OPEN_TRANSACTION_NOT_FOUND);
        }

        OpenTransaction openTransaction = openTransactionOpt.get();

        // Verificar que la transacción esté en estado OPEN
        if (!EtransactionStatus.OPEN.equals(openTransaction.getStatus())) {
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.TRANSACTION_STATUS_NOT_OPEN);
        }

        // Calcular tarifa usando el servicio de BillingPrice (abstrae toda la lógica)
        com.webstore.usersMs.dtos.DBillingPriceCalculationResult calculationResult = billingPriceService
                .calculateBillingPriceForTransaction(
                        openTransaction.getStartDay(),
                        openTransaction.getStartTime(),
                        openTransaction.getTipoVehiculo());

        LocalDateTime endDateTime = LocalDateTime.now();

        // Actualizar transacción abierta a estado CLOSED
        openTransaction.setStatus(EtransactionStatus.CLOSED);
        openTransaction.setEndTime(endDateTime.toLocalDate());
        openTransaction.setEndDate(endDateTime.toLocalTime());
        openTransaction.setTimeElapsed(calculationResult.getTimeElapsed());

        // Asociar la tarifa de referencia y establecer montos calculados
        if (calculationResult.getBillingPrice() != null) {
            openTransaction.setBillingPrice(calculationResult.getBillingPrice());

            // Setear serviceTypeServiceTypeId desde el resultado del cálculo
            if (calculationResult.getServiceTypeServiceTypeId() != null) {
                openTransaction.setServiceTypeServiceTypeId(calculationResult.getServiceTypeServiceTypeId());
                log.info("serviceTypeServiceTypeId asignado desde cálculo de tarifa: {}",
                        calculationResult.getServiceTypeServiceTypeId());
            }
        }

        // Almacenar montos calculados en openTransaction
        openTransaction.setAmount(calculationResult.getTotalAmount().doubleValue());
        openTransaction.setTotalAmount(calculationResult.getTotalAmount().doubleValue());

        log.info(
                "Montos almacenados en openTransaction: amount={}, totalAmount={}, hoursForBilling={}, pricePerHour={}",
                calculationResult.getTotalAmount(), calculationResult.getTotalAmount(),
                calculationResult.getHoursForBilling(), calculationResult.getPricePerHour());

        // Asegurar que el currency esté asignado desde el país de la compañía si no
        // está en openTransaction
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

        // Obtener currency para closedTransaction (usar el de openTransaction o el del
        // país de la compañía)
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
                .status(EtransactionStatus.CLOSED)
                .billingPrice(openTransaction.getBillingPrice())
                .amount(openTransaction.getAmount())
                .discount(openTransaction.getDiscount())
                .totalAmount(openTransaction.getTotalAmount())
                .timeElapsed(calculationResult.getTimeElapsed())
                .operationDate(endDateTime)
                .serviceTypeServiceTypeId(openTransaction.getServiceTypeServiceTypeId())
                .sellerAppUser(openTransaction.getAppUserSeller())
                .sellerName(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName())
                .build();

        ClosedTransaction saved = repository.save(closedTransaction);
        log.info(
                "Transacción cerrada: openTransactionId={}, closedTransactionId={}, horas={}, precioPorHora={}, montoTotal={}",
                openTransactionId, saved.getClosedTransactionId(),
                calculationResult.getHoursForBilling(),
                calculationResult.getPricePerHour(),
                calculationResult.getTotalAmount());

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
        java.util.List<ClosedTransaction> transactions = repository.findTodayTransactions(companyId, startOfDay,
                startOfNextDay);

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
                        .totalAmount(ct.getTotalAmount() != null ? java.math.BigDecimal.valueOf(ct.getTotalAmount())
                                : java.math.BigDecimal.ZERO)
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
