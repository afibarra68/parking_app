package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.entities.OpenTransaction;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.enums.EtransactionStatus;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.mappers.OpenTransactionMapper;
import com.webstore.usersMs.repositories.OpenTransactionRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.OpenTransactionService;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.error.WbException;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class OpenTransactionServiceImp implements OpenTransactionService {

    private final OpenTransactionRepository repository;
    private final CompanyRepository companyRepository;
    private final BillingPriceRepository billingPriceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private final OpenTransactionMapper mapper = Mappers.getMapper(OpenTransactionMapper.class);

    @Override
    public DOpenTransaction create(DOpenTransaction dto) throws WbException {
        OpenTransaction entity = mapper.fromDto(dto);
        
        // Obtener el usuario autenticado de la sesión usando el servicio
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        
        // Asignar la compañía del usuario autenticado si no viene en el DTO
        Company companyEntity = null;
        if (dto.getCompanyCompanyId() == null && authenticatedUser != null && authenticatedUser.getCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyIdWithCountry(authenticatedUser.getCompanyId());
            if (company.isPresent()) {
                companyEntity = company.get();
                entity.setCompany(companyEntity);
                log.info("Compañía asignada automáticamente desde la sesión: {}", authenticatedUser.getCompanyId());
            }
        } else if (dto.getCompanyCompanyId() != null) {
            // Si viene en el DTO, usar la del DTO
            Optional<Company> company = companyRepository.findByCompanyIdWithCountry(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                companyEntity = company.get();
                entity.setCompany(companyEntity);
            }
        }
        
        // Asignar el currency de la empresa (país) si no viene en el DTO
        if (dto.getCurrency() == null && companyEntity != null && companyEntity.getCountry() != null) {
            String countryCurrency = companyEntity.getCountry().getCurrency();
            if (countryCurrency != null && !countryCurrency.isEmpty()) {
                entity.setCurrency(countryCurrency);
                log.info("Currency asignado automáticamente desde la empresa: {}", countryCurrency);
            }
        } else if (dto.getCurrency() != null) {
            // Si viene en el DTO, usar el del DTO
            entity.setCurrency(dto.getCurrency());
        }
        
        // Asignar el usuario vendedor desde la sesión si no viene en el DTO
        if (dto.getAppUserAppUserSeller() == null && authenticatedUser != null && authenticatedUser.getAppUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(authenticatedUser.getAppUserId());
            if (user.isPresent()) {
                entity.setAppUserSeller(user.get());
                log.info("Usuario vendedor asignado automáticamente desde la sesión: {}", authenticatedUser.getAppUserId());
            }
        } else if (dto.getAppUserAppUserSeller() != null) {
            // Si viene en el DTO, usar la del DTO
            Optional<User> user = userRepository.findByAppUserId(dto.getAppUserAppUserSeller());
            user.ifPresent(entity::setAppUserSeller);
        }
        
        // Si viene billingPriceBillingPriceId, cargar el billingPrice y setear el serviceTypeServiceTypeId
        if (dto.getBillingPriceBillingPriceId() != null) {
            Optional<BillingPrice> billingPrice = billingPriceRepository.findByBillingPriceId(dto.getBillingPriceBillingPriceId());
            if (billingPrice.isPresent()) {
                entity.setBillingPrice(billingPrice.get());
                // Setear serviceTypeServiceTypeId desde el businessService del billingPrice
                if (billingPrice.get().getBusinessService() != null && 
                    billingPrice.get().getBusinessService().getBusinessServiceId() != null) {
                    entity.setServiceTypeServiceTypeId(billingPrice.get().getBusinessService().getBusinessServiceId());
                    log.info("serviceTypeServiceTypeId asignado desde billingPrice: {}", 
                        billingPrice.get().getBusinessService().getBusinessServiceId());
                }
            }
        }
        
        // El backend agrega automáticamente la fecha y hora de inicio
        LocalDateTime now = LocalDateTime.now();
        entity.setStartDay(now.toLocalDate());
        entity.setStartTime(now.toLocalTime());
        entity.setOperationDate(now);
        entity.setStatus(EtransactionStatus.OPEN);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DOpenTransaction update(DOpenTransaction dto) throws WbException {
        Optional<OpenTransaction> entity = repository.findByOpenTransactionIdAndCompanyCompanyId(dto.getOpenTransactionId(), dto.getCompanyCompanyId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        
        OpenTransaction dbTransaction = entity.get();
        OpenTransaction merged = mapper.merge(dto, dbTransaction);
        
        // Update relationships if IDs are provided
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            company.ifPresent(merged::setCompany);
        }
        
        if (dto.getBillingPriceBillingPriceId() != null) {
            Optional<BillingPrice> billingPrice = billingPriceRepository.findByBillingPriceId(dto.getBillingPriceBillingPriceId());
            if (billingPrice.isPresent()) {
                merged.setBillingPrice(billingPrice.get());
                // Setear serviceTypeServiceTypeId desde el businessService del billingPrice
                if (billingPrice.get().getBusinessService() != null && 
                    billingPrice.get().getBusinessService().getBusinessServiceId() != null) {
                    merged.setServiceTypeServiceTypeId(billingPrice.get().getBusinessService().getBusinessServiceId());
                    log.info("serviceTypeServiceTypeId actualizado desde billingPrice: {}", 
                        billingPrice.get().getBusinessService().getBusinessServiceId());
                }
            }
        }
        
        if (dto.getAppUserAppUserSeller() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getAppUserAppUserSeller());
            user.ifPresent(merged::setAppUserSeller);
        }
        
        return mapper.toDto(repository.save(merged));
    }

    @Override
    public Page<DOpenTransaction> findBy(String status, Long companyCompanyId, Pageable pageable) {
        return mapper.toPage(repository.findBy(status, companyCompanyId, pageable));
    }

    @Override
    public DOpenTransaction findByVehiclePlate(String vehiclePlate) throws WbException {
        // Obtener el usuario autenticado para obtener el companyId
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Usuario no autenticado al intentar buscar vehículo por placa: {}", vehiclePlate);
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }
        
        if (authenticatedUser.getCompanyId() == null) {
            log.error("Usuario autenticado sin companyId. appUserId: {}", authenticatedUser.getAppUserId());
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED);
        }

        Optional<OpenTransaction> transaction = repository.findByVehiclePlateAndCompanyId(
            vehiclePlate, 
            authenticatedUser.getCompanyId()
        );

        if (transaction.isEmpty()) {
            log.warn("No se encontró transacción abierta para placa: {} y companyId: {}", 
                vehiclePlate, authenticatedUser.getCompanyId());
            throw new WbException(CLIENT_NOT_FOUND);
        }

        return mapper.toDto(transaction.get());
    }
}

