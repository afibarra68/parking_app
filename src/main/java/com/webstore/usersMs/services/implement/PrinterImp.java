package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Printer;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.PrinterMapper;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.PrinterRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.PrinterInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrinterImp implements PrinterInterface {

    private final PrinterRepository repository;

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final PrinterMapper mapper = Mappers.getMapper(PrinterMapper.class);

    @Override
    public DPrinter create(DPrinter dto) throws WbException {
        Printer entity = mapper.fromDto(dto);
        
        // Set relationships
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                entity.setCompany(company.get());
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                entity.setUser(user.get());
            }
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());
        
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DPrinter update(DPrinter dto) throws WbException {
        Optional<Printer> entityOptional = repository.findByPrinterId(dto.getPrinterId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        Printer dbPrinter = entityOptional.get();
        mapper.merge(dto, dbPrinter);
        
        // Update relationships
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                dbPrinter.setCompany(company.get());
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                dbPrinter.setUser(user.get());
            }
        }
        
        dbPrinter.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(dbPrinter));
    }

    public DPrinter getPrinterByBusinessModel() {

    }

    @Override
    public List<DPrinter> getBy(Long printerId, String printerName, String printerType, Long companyCompanyId, Long userUserId, Boolean isActive) throws WbException {
        List<Printer> data = repository.findBy(printerId, printerName, printerType, companyCompanyId, userUserId, isActive);
        return mapper.toList(data);
    }

    @Override
    public Page<DPrinter> findByPageable(Long printerId, String printerName, String printerType, Long companyCompanyId, Long userUserId, Boolean isActive, Pageable pageable) {
        return repository.findByPageable(printerId, printerName, printerType, companyCompanyId, userUserId, isActive, pageable).map(mapper::toDto);
    }
}

