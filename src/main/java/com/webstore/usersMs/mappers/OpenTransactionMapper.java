package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.entities.OpenTransaction;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface OpenTransactionMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    OpenTransaction fromDto(DOpenTransaction dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "billingPriceBillingPriceId", source = "billingPrice.billingPriceId")
    @Mapping(target = "appUserAppUserSeller", source = "appUserSeller.appUserId")
    DOpenTransaction toDto(OpenTransaction entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    OpenTransaction merge(DOpenTransaction dto, @MappingTarget OpenTransaction transaction);

    default List<DOpenTransaction> toList(List<OpenTransaction> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DOpenTransaction> toPage(Page<OpenTransaction> page) {
        return page.map(this::toDto);
    }
}

