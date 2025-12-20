package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.COMPANY_SELLER_NOT_FOUND;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.dtos.DBillingPriceCalculationResult;
import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.dtos.DMapField;
import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.services.BillingPriceService;
import com.webstore.usersMs.services.CompanyService;
import com.webstore.usersMs.services.TicketTemplateInterface;
import org.apache.commons.lang3.tuple.Triple;
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
import com.webstore.usersMs.services.TemplatePrinterService;
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

    private final CompanyService companyService;

    private final BillingPriceService billingPriceService;

    private final UserRepository userRepository;

    private final UserService userService;

    private final TemplatePrinterService templatePrinterService;

    private final TicketTemplateInterface templatesService;

    private final OpenTransactionMapper mapper = Mappers.getMapper(OpenTransactionMapper.class);

    @Override
    public DOpenTransaction create(DOpenTransaction dto) throws WbException {
        LocalDateTime now = LocalDateTime.now();
        UserLogin authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.getCompanyId() == null) {
            throw new WbException(COMPANY_SELLER_NOT_FOUND);
        }
        OpenTransaction entity = mapper.fromDto(dto);
        DCompany company = companyService.getCurrentUserCompany();
        Optional<User> user = userRepository.findByAppUserId(authenticatedUser.getAppUserId());
        DTicketTemplate template = templatesService.getByReceiptModel(EReceiptModel.IN, dto.getServiceTypeServiceTypeId(), authenticatedUser);

        entity.setStatus(EtransactionStatus.OPEN);
        entity.setCurrency(company.getCountry().getCurrency());
        entity.setAppUserSeller(user.get());
        entity.setStartDay(now.toLocalDate());
        entity.setStartTime(now.toLocalTime());
        entity.setOperationDate(now);

        OpenTransaction savedEntity = repository.save(entity);
        DOpenTransaction result = mapper.toDto(savedEntity);

        DMapField printer = mapper.toPrinter(result);

        try {
            String buildTicket = templatePrinterService.buildTicket(EReceiptModel.IN, printer, template);
            result.setBuildTicket(buildTicket);
            log.info("Ticket construido exitosamente para openTransactionId: {}", savedEntity.getOpenTransactionId());
        } catch (Exception e) {
            log.error("Error al construir el ticket para openTransactionId: {}",
                    savedEntity.getOpenTransactionId(), e);
            // No lanzar excepción, solo registrar el error y continuar
            result.setBuildTicket("");
        }

        return result;
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

