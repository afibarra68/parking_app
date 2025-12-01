package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Discount;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.BillingPriceMapper;
import com.webstore.usersMs.repositories.BillingPriceRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.DiscountRepository;
import com.webstore.usersMs.services.BillingPriceService;

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

    private final BillingPriceMapper mapper = Mappers.getMapper(BillingPriceMapper.class);

    @Override
    public DBillingPrice create(DBillingPrice dto) throws WbException {
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

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DBillingPrice update(DBillingPrice dto) throws WbException {
        Optional<BillingPrice> entity = repository.findByBillingPriceId(dto.getBillingPriceId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }

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

        return mapper.toDto(repository.save(merged));
    }

    @Override
    public List<DBillingPrice> getBy(String status, Long companyCompanyId, String coverType) throws WbException {
        List<BillingPrice> data = repository.findBy(status, companyCompanyId, coverType);
        return mapper.toList(data);
    }

    @Override
    public Page<DBillingPrice> findByPageable(String status, Long companyCompanyId, String coverType, Pageable pageable) {
        return mapper.toPage(repository.findByPageable(status, companyCompanyId, coverType, pageable));
    }
}

