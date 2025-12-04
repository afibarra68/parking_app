package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_RANGE_OVERLAP;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_TIME_EXCEEDED;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.BILLING_PRICE_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Discount;
import com.webstore.usersMs.entities.BusinessService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.BillingPriceMapper;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.DiscountRepository;
import com.webstore.usersMs.repositories.BusinessServiceRepository;
import com.webstore.usersMs.services.BillingPriceService;
import com.webstore.usersMs.services.UserService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BillingPriceServiceImp implements BillingPriceService {

    private final BillingPriceRepository repository;
    private final CompanyRepository companyRepository;
    private final DiscountRepository discountRepository;
    private final BusinessServiceRepository businessServiceRepository;
    private final UserService userService;

    private final BillingPriceMapper mapper = Mappers.getMapper(BillingPriceMapper.class);

    @Override
    public DBillingPrice create(DBillingPrice dto) throws WbException {
        // Validar que no se crucen los rangos para el mismo tipo de vehículo
        validateRangeOverlap(dto, null);
        
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
            Optional<BusinessService> businessService = businessServiceRepository.findByBusinessServiceId(dto.getBusinessServiceBusinessServiceId());
            businessService.ifPresent(entity::setBusinessService);
        }

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DBillingPrice update(DBillingPrice dto) throws WbException {
        Optional<BillingPrice> entity = repository.findByBillingPriceId(dto.getBillingPriceId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }

        // Validar que no se crucen los rangos para el mismo tipo de vehículo
        validateRangeOverlap(dto, dto.getBillingPriceId());

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
            Optional<BusinessService> businessService = businessServiceRepository.findByBusinessServiceId(dto.getBusinessServiceBusinessServiceId());
            businessService.ifPresent(merged::setBusinessService);
        } else {
            merged.setBusinessService(null);
        }

        return mapper.toDto(repository.save(merged));
    }

    /**
     * Valida que no existan rangos solapados para el mismo tipo de vehículo y empresa.
     * Dos rangos se solapan si:
     * - El inicio del nuevo rango está dentro de un rango existente
     * - El fin del nuevo rango está dentro de un rango existente
     * - Un rango existente está completamente dentro del nuevo rango
     * - El nuevo rango está completamente dentro de un rango existente
     */
    private void validateRangeOverlap(DBillingPrice dto, Long excludeBillingPriceId) throws WbException {
        // Solo validar si se proporcionan los campos necesarios
        if (dto.getCompanyCompanyId() == null || 
            dto.getTipoVehiculo() == null || 
            dto.getStart() == null || 
            dto.getEnd() == null) {
            return; // No validar si faltan datos requeridos
        }

        // Validar que start <= end
        if (dto.getStart() > dto.getEnd()) {
            throw new WbException(BILLING_PRICE_RANGE_OVERLAP);
        }

        // Buscar rangos solapados
        List<BillingPrice> overlappingRanges = repository.findOverlappingRanges(
            dto.getCompanyCompanyId(),
            dto.getTipoVehiculo(),
            dto.getStart(),
            dto.getEnd(),
            excludeBillingPriceId
        );

        if (!overlappingRanges.isEmpty()) {
            log.warn("Intento de crear/actualizar precio con rango solapado. " +
                    "CompanyId: {}, TipoVehiculo: {}, Rango: {}-{}, ExcluirId: {}",
                    dto.getCompanyCompanyId(), dto.getTipoVehiculo(), 
                    dto.getStart(), dto.getEnd(), excludeBillingPriceId);
            throw new WbException(BILLING_PRICE_RANGE_OVERLAP);
        }
    }

    @Override
    public List<DBillingPrice> getBy(String status, Long companyCompanyId, String tipoVehiculo) throws WbException {
        List<BillingPrice> data = repository.findBy(status, companyCompanyId, tipoVehiculo);
        return mapper.toList(data);
    }

    @Override
    public Page<DBillingPrice> findByPageable(String status, Long companyCompanyId, String tipoVehiculo, Pageable pageable) {
        return mapper.toPage(repository.findByPageable(status, companyCompanyId, tipoVehiculo, pageable));
    }

    @Override
    public DBillingPrice calculatePriceByHours(Integer hours, String tipoVehiculo) throws WbException {
        // Obtener el usuario autenticado para obtener el companyId
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Usuario no autenticado al intentar calcular tarifa para {} horas", hours);
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }
        
        if (authenticatedUser.getCompanyId() == null) {
            log.error("Usuario autenticado sin companyId. appUserId: {}", authenticatedUser.getAppUserId());
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        // Validar que el tipo de vehículo esté presente
        if (tipoVehiculo == null || tipoVehiculo.trim().isEmpty()) {
            log.error("Tipo de vehículo no proporcionado al calcular tarifa");
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Buscar el rango máximo configurado para este tipo de vehículo
        Integer maxEnd = repository.findMaxEndByCompanyAndTipoVehiculo(
            authenticatedUser.getCompanyId(),
            tipoVehiculo
        );

        // Validar que exista al menos un rango configurado
        if (maxEnd == null) {
            log.warn("No se encontró tarifa configurada para tipoVehiculo: {} y companyId: {}", 
                tipoVehiculo, authenticatedUser.getCompanyId());
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        // Validar que el tiempo no exceda el rango máximo
        if (hours > maxEnd) {
            log.warn("Tiempo excedido: {} horas excede el rango máximo {} para tipoVehiculo: {} y companyId: {}", 
                hours, maxEnd, tipoVehiculo, authenticatedUser.getCompanyId());
            throw new WbException(BILLING_PRICE_TIME_EXCEEDED);
        }

        // Buscar tarifa según horas, companyId y tipo de vehículo
        Optional<BillingPrice> billingPrice = repository.findPriceByHoursCompanyAndTipoVehiculo(
            hours,
            authenticatedUser.getCompanyId(),
            tipoVehiculo
        );

        if (billingPrice.isEmpty()) {
            log.warn("No se encontró tarifa para {} horas, tipoVehiculo: {} y companyId: {}", 
                hours, tipoVehiculo, authenticatedUser.getCompanyId());
            throw new WbException(BILLING_PRICE_NOT_FOUND);
        }

        return mapper.toDto(billingPrice.get());
    }
}

