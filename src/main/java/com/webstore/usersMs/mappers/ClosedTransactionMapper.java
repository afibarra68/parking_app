package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.entities.ClosedTransaction;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ClosedTransactionMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "sellerAppUser", ignore = true)
    ClosedTransaction fromDto(DClosedTransaction dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "billingPriceBillingPriceId", source = "billingPrice.billingPriceId")
    @Mapping(target = "sellerAppUserId", source = "sellerAppUser.appUserId")
    DClosedTransaction toDto(ClosedTransaction entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "sellerAppUser", ignore = true)
    ClosedTransaction merge(DClosedTransaction dto, @MappingTarget ClosedTransaction transaction);

    default List<DClosedTransaction> toList(List<ClosedTransaction> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DClosedTransaction> toPage(Page<ClosedTransaction> page) {
        return page.map(this::toDto);
    }
}

