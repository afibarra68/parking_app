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
import com.webstore.usersMs.services.ClosedTransactionService;
import com.webstore.usersMs.error.WbException;

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
        return mapper.toPage(repository.findBy(status, companyCompanyId, pageable));
    }
}

