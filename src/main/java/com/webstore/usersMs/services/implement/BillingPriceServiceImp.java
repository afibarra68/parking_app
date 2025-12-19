package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_RANGE_OVERLAP;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_NOT_FOUND;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.dtos.DBillingPriceCalculationResult;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Discount;
import com.webstore.usersMs.entities.BusinessService;
import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.BillingPriceMapper;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.DiscountRepository;
import com.webstore.usersMs.repositories.BusinessServiceRepository;
import com.webstore.usersMs.services.BillingPriceService;
import com.webstore.usersMs.services.UserService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@AllArgsConstructor
public class BillingPriceServiceImp implements BillingPriceService {

    private final BillingPriceRepository repository;

    private final CompanyRepository companyRepository;

    private final DiscountRepository discountRepository;

    private final BusinessServiceRepository businessServiceRepository;

    private final UserService userService;

    private final BillingPriceMapper mapper = Mappers.getMapper(BillingPriceMapper.class);

    @Override
    public DBillingPrice create(DBillingPrice dto) throws WbException {
        // Validar que no exista duplicado de horas para el mismo tipo de vehículo
        validateDuplicateHours(dto, null);

        BillingPrice entity = mapper.fromDto(dto);

        // Set relationships if IDs are provided
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(entity::setCompany);
        }

        if (dto.getDiscountDiscountId() != null) {
            Optional<Discount> discount = discountRepository.findByDiscountId(dto.getDiscountDiscountId());
            discount.ifPresent(entity::setDiscount);
        }

        if (dto.getBusinessServiceBusinessServiceId() != null) {
            Optional<BusinessService> businessService = businessServiceRepository
                    .findByBusinessServiceId(dto.getBusinessServiceBusinessServiceId());
            businessService.ifPresent(entity::setBusinessService);
        }

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public void delete(Long billingPriceId) throws WbException {
        Optional<BillingPrice> entity = repository.findByBillingPriceId(billingPriceId);
        if (entity.isEmpty()) {
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }
        repository.deleteById(billingPriceId);
        log.info("Tarifa eliminada: billingPriceId={}", billingPriceId);
    }

    @Override
    public DBillingPrice update(DBillingPrice dto) throws WbException {
        Optional<BillingPrice> entity = repository.findByBillingPriceId(dto.getBillingPriceId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        // Validar que no exista duplicado de horas para el mismo tipo de vehículo
        validateDuplicateHours(dto, dto.getBillingPriceId());

        BillingPrice dbBillingPrice = entity.get();
        BillingPrice merged = mapper.merge(dto, dbBillingPrice);

        // Update relationships if IDs are provided
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(merged::setCompany);
        } else {
            merged.setCompany(null);
        }

        if (dto.getDiscountDiscountId() != null) {
            Optional<Discount> discount = discountRepository.findByDiscountId(dto.getDiscountDiscountId());
            discount.ifPresent(merged::setDiscount);
        } else {
            merged.setDiscount(null);
        }

        if (dto.getBusinessServiceBusinessServiceId() != null) {
            Optional<BusinessService> businessService = businessServiceRepository
                    .findByBusinessServiceId(dto.getBusinessServiceBusinessServiceId());
            businessService.ifPresent(merged::setBusinessService);
        } else {
            merged.setBusinessService(null);
        }

        return mapper.toDto(repository.save(merged));
    }

    /**
     * Valida que no existan horas duplicadas para el mismo tipo de vehículo y
     * empresa.
     */
    private void validateDuplicateHours(DBillingPrice dto, Long excludeBillingPriceId) throws WbException {
        // Solo validar si se proporcionan los campos necesarios
        if (dto.getCompanyCompanyId() == null ||
                dto.getTipoVehiculo() == null ||
                dto.getHours() == null) {
            return; // No validar si faltan datos requeridos
        }

        // Validar que hours sea positivo
        if (dto.getHours() <= 0) {
            throw new WbException(BILLING_PRICE_RANGE_OVERLAP);
        }

        // Buscar horas duplicadas


        List<BillingPrice> duplicateHours = repository.findDuplicateHours(
                dto.getCompanyCompanyId(),
                ETipoVehiculo.valueOf(dto.getTipoVehiculo().getId()),
                dto.getHours(),
                excludeBillingPriceId);

        if (!duplicateHours.isEmpty()) {
            throw new WbException(BILLING_PRICE_RANGE_OVERLAP);
        }
    }

    @Override
    public List<DBillingPrice> getBy(EBillingStatus status, Long companyCompanyId, ETipoVehiculo tipoVehiculo) throws WbException {
        // Convertir String a enums
        EBillingStatus statusEnum = null;

        List<BillingPrice> data = repository.findBy(statusEnum, companyCompanyId, tipoVehiculo);
        return mapper.toList(data);
    }

    @Override
    public Page<DBillingPrice> findByPageable(EBillingStatus status, Long companyCompanyId, ETipoVehiculo tipoVehiculo,
            Pageable pageable) {

        return mapper.toPage(repository.findByPageable(status, companyCompanyId, tipoVehiculo, pageable));
    }

    @Override
    public DBillingPrice calculatePriceByHours(Integer hours, String tipoVehiculo) throws WbException {
        // Validar parámetros de entrada
        if (hours == null || hours < 1) {
            log.error("Horas inválidas para calcular tarifa: {}", hours);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Obtener el usuario autenticado para obtener el companyId
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Usuario no autenticado al intentar calcular tarifa para {} horas, tipoVehiculo: {}",
                    hours, tipoVehiculo);
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        if (authenticatedUser.getCompanyId() == null) {
            log.error("Usuario autenticado sin companyId. appUserId: {}", authenticatedUser.getAppUserId());
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        Long companyId = authenticatedUser.getCompanyId();

        // Validar que el tipo de vehículo esté presente
        if (tipoVehiculo == null || tipoVehiculo.trim().isEmpty()) {
            log.error("Tipo de vehículo no proporcionado al calcular tarifa. Hours: {}, CompanyId: {}",
                    hours, companyId);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Normalizar tipo de vehículo (trim y uppercase para consistencia)
        String tipoVehiculoNormalized = tipoVehiculo.trim().toUpperCase();

        log.info("Calculando tarifa: hours={}, tipoVehiculo={}, companyId={}",
                hours, tipoVehiculoNormalized, companyId);

        // PASO 1: Buscar tarifa exacta por horas, tipo de vehículo y companyId
        ETipoVehiculo tipoVehiculoEnum;
        try {
            tipoVehiculoEnum = ETipoVehiculo.valueOf(tipoVehiculoNormalized);
        } catch (IllegalArgumentException e) {
            log.error("Tipo de vehículo inválido: {}. Valores válidos: {}", tipoVehiculoNormalized,
                    Arrays.toString(ETipoVehiculo.values()));
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }
        Optional<BillingPrice> billingPrice = repository.findPriceByHoursCompanyAndTipoVehiculo(
                hours,
                companyId,
                tipoVehiculoEnum,
                EBillingStatus.ACTIVE);

        if (billingPrice.isPresent()) {
            log.info("Tarifa exacta encontrada: billingPriceId={}, hours={}, mount={}, tipoVehiculo={}",
                    billingPrice.get().getBillingPriceId(),
                    billingPrice.get().getHours(),
                    billingPrice.get().getMount(),
                    tipoVehiculoNormalized);
            return mapper.toDto(billingPrice.get());
        }

        // PASO 2: Si no se encuentra tarifa exacta, buscar la más cercana menor o igual
        log.debug("No se encontró tarifa exacta para {} horas. Buscando tarifa más cercana...", hours);
        List<BillingPrice> availablePrices = repository.findPriceByHoursLessOrEqual(
                hours,
                companyId,
                tipoVehiculoEnum,
                EBillingStatus.ACTIVE);

        if (availablePrices.isEmpty()) {
            log.warn("No se encontró tarifa para {} horas o menos, tipoVehiculo: {} y companyId: {}. " +
                    "No hay tarifas configuradas para este tipo de vehículo.",
                    hours, tipoVehiculoNormalized, companyId);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Tomar la tarifa con más horas (la más cercana menor o igual)
        // La consulta ya ordena por hours DESC, así que el primer elemento es el más
        // cercano
        BillingPrice selectedPrice = availablePrices.get(0);
        log.info("Tarifa más cercana encontrada: billingPriceId={}, hours={}, mount={}, tipoVehiculo={} " +
                "(solicitadas: {} horas)",
                selectedPrice.getBillingPriceId(),
                selectedPrice.getHours(),
                selectedPrice.getMount(),
                tipoVehiculoNormalized,
                hours);

        return mapper.toDto(selectedPrice);
    }

    @Override
    public Double getPricePerHourByTipoVehiculo(ETipoVehiculo tipoVehiculo) throws WbException {

        UserLogin authenticatedUser = userService.getAuthenticatedUser();

        Long companyId = authenticatedUser.getCompanyId();

        List<BillingPrice> billingPrices = repository.findPricePerHourByTipoVehiculo(
                companyId,
                tipoVehiculo,
                EBillingStatus.ACTIVE,
                LocalDate.now());

        if (billingPrices.isEmpty()) {
            log.warn("No se encontró tarifa de 1 hora para tipoVehiculo: {} y companyId: {}. " +
                    "No hay tarifas configuradas para este tipo de vehículo.",
                    tipoVehiculo, companyId);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Usar stream para encontrar la tarifa más cercana a dateStartDisabled (la que
        // tiene la fecha de finalización más próxima)
        LocalDate currentDate = LocalDate.now();
        BillingPrice price = billingPrices.stream()
                .filter(bp -> bp.getDateStartDisabled() != null && bp.getDateStartDisabled().isAfter(currentDate))
                .max(Comparator.comparing(BillingPrice::getDateStartDisabled).reversed())
                .orElseThrow(() -> new WbException(BILLING_PRICE_NOT_FOUND));

        // Calcular precio por hora: mount / hours
        if (price.getMount() == null || price.getHours() == null || price.getHours() <= 0) {
            log.error("Tarifa inválida: mount={}, hours={}, billingPriceId={}",
                    price.getMount(), price.getHours(), price.getBillingPriceId());
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        double pricePerHour = (double) price.getMount() / price.getHours();

        log.info("Precio por hora obtenido: tipoVehiculo={}, pricePerHour={}, billingPriceId={}, dateStartDisabled={}",
                tipoVehiculo, pricePerHour, price.getBillingPriceId(), price.getDateStartDisabled());

        return pricePerHour;
    }

    /**
     * Calcula la tarifa completa para una transacción basándose en:
     * - Fecha y hora de inicio de la transacción
     * - Tipo de vehículo
     * - Fecha y hora actual (fin de la transacción)
     * 
     * Este método abstrae toda la lógica de cálculo de tarifas:
     * 1. Calcula las horas transcurridas
     * 2. Obtiene el precio por hora según el tipo de vehículo
     * 3. Calcula el monto total
     * 4. Obtiene la tarifa de referencia y el servicio de negocio
     * 
     * El companyId se obtiene automáticamente del usuario autenticado.
     * 
     * @param startDay     Fecha de inicio de la transacción
     * @param startTime    Hora de inicio de la transacción
     * @param tipoVehiculo Tipo de vehículo (String o Enum name)
     * @return DBillingPriceCalculationResult con toda la información calculada
     * @throws WbException Si no se encuentra tarifa, hay error de autenticación o
     *                     datos inválidos
     */
    @Override
    public DBillingPriceCalculationResult calculateBillingPriceForTransaction(
            java.time.LocalDate startDay,
            java.time.LocalTime startTime,
            ETipoVehiculo tipoVehiculo) throws WbException {

        // Validar parámetros de entrada
        if (startDay == null || startTime == null) {
            log.error("Fecha o hora de inicio inválidas: startDay={}, startTime={}", startDay, startTime);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        if (authenticatedUser.getCompanyId() == null) {
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        Long companyId = authenticatedUser.getCompanyId();

        LocalDateTime startDateTime = LocalDateTime.of(startDay, startTime);
        LocalDateTime endDateTime = LocalDateTime.now();
        Duration duration = Duration.between(startDateTime, endDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        // Calcular horas para la facturación (redondeo hacia arriba)
        // Si hay minutos adicionales, se cuenta como una hora más
        int hoursForBilling = (int) (hours + (minutes > 0 ? 1 : 0));
        if (hoursForBilling == 0) {
            hoursForBilling = 1; // Mínimo 1 hora
        }

        // Formatear tiempo transcurrido
        String timeElapsed = String.format("%d horas %d minutos", hours, minutes);

        // Obtener precio por hora basado en el tipo de vehículo
        Double pricePerHour = getPricePerHourByTipoVehiculo(tipoVehiculo);

        if (pricePerHour == null || pricePerHour <= 0) {
            log.error("Precio por hora no encontrado o inválido para tipoVehiculo: {}", tipoVehiculo);
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Calcular monto total: precio por hora * horas transcurridas
        double totalAmount = pricePerHour * hoursForBilling;
        long totalAmountLong = Math.round(totalAmount);

        log.info("Cálculo de tarifa: tipoVehiculo={}, hoursForBilling={}, pricePerHour={}, totalAmount={}",
                tipoVehiculo, hoursForBilling, pricePerHour, totalAmountLong);

        // Obtener la tarifa de 1 hora para asociarla a la transacción (para mantener
        // referencia)
        List<BillingPrice> billingPrices = repository.findPricePerHourByTipoVehiculo(
                companyId,
                tipoVehiculo,
                EBillingStatus.ACTIVE,
                LocalDate.now());

        BillingPrice billingPriceEntity = null;
        Long serviceTypeServiceTypeId = null;

        if (!billingPrices.isEmpty()) {
            // Usar stream para encontrar la tarifa más cercana a dateStartDisabled (la que
            // tiene la fecha de finalización más próxima)
            LocalDate currentDate = LocalDate.now();
            billingPriceEntity = billingPrices.stream()
                    .filter(bp -> bp.getDateStartDisabled() != null && bp.getDateStartDisabled().isAfter(currentDate))
                    .max(Comparator.comparing(BillingPrice::getDateStartDisabled).reversed())
                    .orElse(null);

            // Obtener serviceTypeServiceTypeId desde el businessService del billingPrice
            if (billingPriceEntity != null &&
                    billingPriceEntity.getBusinessService() != null &&
                    billingPriceEntity.getBusinessService().getBusinessServiceId() != null) {
                serviceTypeServiceTypeId = billingPriceEntity.getBusinessService().getBusinessServiceId();
                log.info("serviceTypeServiceTypeId obtenido desde billingPrice: {}, dateStartDisabled: {}",
                        serviceTypeServiceTypeId, billingPriceEntity.getDateStartDisabled());
            }
        }

        // Construir y retornar el resultado
        return DBillingPriceCalculationResult.builder()
                .hoursForBilling(hoursForBilling)
                .pricePerHour(pricePerHour)
                .totalAmount(totalAmountLong)
                .timeElapsed(timeElapsed)
                .billingPrice(billingPriceEntity)
                .serviceTypeServiceTypeId(serviceTypeServiceTypeId)
                .tipoVehiculo(com.webstore.usersMs.utils.EnumDataMapperUtils.mapToEnumResource(tipoVehiculo))
                .build();
    }
}
