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
import com.webstore.usersMs.mappers.OpenTransactionMapper;
import com.webstore.usersMs.repositories.OpenTransactionRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.OpenTransactionService;
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

    private final OpenTransactionMapper mapper = Mappers.getMapper(OpenTransactionMapper.class);

    @Override
    public DOpenTransaction create(DOpenTransaction dto) throws WbException {
        OpenTransaction entity = mapper.fromDto(dto);
        
        // El backend agrega automáticamente la fecha y hora de inicio
        LocalDateTime now = LocalDateTime.now();
        entity.setStartDay(now.toLocalDate());
        entity.setStartTime(now.toLocalTime());
        entity.setOperationDate(now);
        
        // Establecer estado por defecto si no viene
        if (entity.getStatus() == null || entity.getStatus().isEmpty()) {
            entity.setStatus("OPEN");
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DOpenTransaction update(DOpenTransaction dto) throws WbException {
        Optional<OpenTransaction> entity = repository.findByOpenTransactionId(dto.getOpenTransactionId());
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
            billingPrice.ifPresent(merged::setBillingPrice);
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
}

