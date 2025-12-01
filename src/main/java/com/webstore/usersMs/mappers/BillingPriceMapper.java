package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.entities.BillingPrice;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface BillingPriceMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "discount", ignore = true)
    BillingPrice fromDto(DBillingPrice dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "discountDiscountId", source = "discount.discountId")
    DBillingPrice toDto(BillingPrice entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "discount", ignore = true)
    BillingPrice merge(DBillingPrice dto, @MappingTarget BillingPrice billingPrice);

    default List<DBillingPrice> toList(List<BillingPrice> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DBillingPrice> toPage(Page<BillingPrice> page) {
        return page.map(this::toDto);
    }
}

