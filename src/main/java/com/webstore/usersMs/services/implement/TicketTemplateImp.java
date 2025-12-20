package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.dtos.DTicketTemplateOptions;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Printer;
import com.webstore.usersMs.entities.TicketTemplate;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.enums.ELargeVariableTicket;
import com.webstore.usersMs.entities.enums.EPrinterType;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.entities.enums.ETicketType;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.TicketTemplateMapper;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.PrinterRepository;
import com.webstore.usersMs.repositories.TicketTemplateRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.TicketTemplateInterface;
import com.webstore.usersMs.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Optional;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.NO_TEMPLATE_FLAT_FOR_BUILDING;

@Service
@Log4j2
@RequiredArgsConstructor
public class TicketTemplateImp implements TicketTemplateInterface {

    private final TicketTemplateRepository repository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PrinterRepository printerRepository;
    private final UserService userService;

    private final TicketTemplateMapper mapper = Mappers.getMapper(TicketTemplateMapper.class);

    @Override
    public DTicketTemplate create(DTicketTemplate dto) throws WbException {
        TicketTemplate entity = mapper.fromDto(dto);
        
        // Obtener el usuario autenticado de la sesión usando el servicio
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        
        // Asignar la compañía del usuario autenticado si no viene en el DTO
        Company companyEntity = null;
        if (dto.getCompanyCompanyId() == null && authenticatedUser != null && authenticatedUser.getCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(authenticatedUser.getCompanyId());
            if (company.isPresent()) {
                companyEntity = company.get();
                entity.setCompany(companyEntity);
                log.info("Compañía asignada automáticamente desde la sesión: {}", authenticatedUser.getCompanyId());
            }
        } else if (dto.getCompanyCompanyId() != null) {
            // Si viene en el DTO, usar la del DTO
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                companyEntity = company.get();
                entity.setCompany(companyEntity);
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                entity.setUser(user.get());
            }
        }
        
        // Asignar la impresora si viene en el DTO
        if (dto.getPrinterPrinterId() != null) {
            Optional<Printer> printer = printerRepository.findByPrinterId(dto.getPrinterPrinterId());
            if (printer.isPresent()) {
                entity.setPrinter(printer.get());
            }
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DTicketTemplate update(DTicketTemplate dto) throws WbException {
        Optional<TicketTemplate> entityOptional = repository.findByTicketTemplateId(dto.getTicketTemplateId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        TicketTemplate dbTicketTemplate = entityOptional.get();
        mapper.merge(dto, dbTicketTemplate);
        
        // Update relationships
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                dbTicketTemplate.setCompany(company.get());
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                dbTicketTemplate.setUser(user.get());
            }
        }
        
        // Actualizar la relación con la impresora
        if (dto.getPrinterPrinterId() != null) {
            Optional<Printer> printer = printerRepository.findByPrinterId(dto.getPrinterPrinterId());
            if (printer.isPresent()) {
                dbTicketTemplate.setPrinter(printer.get());
            }
        } else {
            // Si no viene printerId, eliminar la relación
            dbTicketTemplate.setPrinter(null);
        }
        
        dbTicketTemplate.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(dbTicketTemplate));
    }

    @Override
    public List<DTicketTemplate> getBy(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId) throws WbException {
        List<TicketTemplate> data = repository.findBy(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId);
        return mapper.toList(data);
    }

    @Override
    public DTicketTemplate getByReceiptModel(EReceiptModel receipt, Long serviceId, UserLogin authenticatedUser) throws WbException {
        DTicketTemplateOptions options =  getModelReceiptType(receipt, authenticatedUser);
         Optional<TicketTemplate>  template = repository.findByOptions(
                options.getPrinterType(),
                options.getTicketType(),
                options.getUserServiceId(),
                options.isValidateAvailablePrinters(),
                options.getCompanyId()
        );

        if (template.isPresent()) {
           return mapper.toDto(template.get());
        }
        throw new WbException(NO_TEMPLATE_FLAT_FOR_BUILDING);

    }


    @Override
    public Page<DTicketTemplate> findByPageable(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId, Pageable pageable) {
        return repository.findByPageable(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId, pageable).map(mapper::toDto);
    }

    protected DTicketTemplateOptions getModelReceiptType(EReceiptModel model, UserLogin authenticatedUser) {

        if (EReceiptModel.IN.equals(model)) {

            return DTicketTemplateOptions.builder()
                    .printerType(EPrinterType.WINDOWS)
                    .ticketType(ETicketType.ENTRANCE)
                    .variableTicket(ELargeVariableTicket.E58MM)
                    .validateAvailablePrinters(true)
                    .userServiceId(authenticatedUser.getAppUserId())
                    .companyId(authenticatedUser.getCompanyId())
                    .build();

        } else if (EReceiptModel.OUT.equals(model))  {

            return DTicketTemplateOptions.builder()
                    .printerType(EPrinterType.WINDOWS)
                    .ticketType(ETicketType.LEAVING_PARKING)
                    .variableTicket(ELargeVariableTicket.E88MM)
                    .validateAvailablePrinters(true)
                    .build();
        }

        return DTicketTemplateOptions.builder()
                .printerType(EPrinterType.WINDOWS)
                .ticketType(ETicketType.INVOICE_FINAL)
                .variableTicket(ELargeVariableTicket.E88MM)
                .validateAvailablePrinters(true)
                .sendMail(true)
                .build();

    }
}

