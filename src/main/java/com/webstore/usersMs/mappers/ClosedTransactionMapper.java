package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.entities.ClosedTransaction;
import com.webstore.usersMs.entities.enums.EtransactionStatus;
import com.webstore.usersMs.error.handlers.enums.EnumResource;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ClosedTransactionMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "sellerAppUser", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "enumResourceToEtransactionStatus")
    ClosedTransaction fromDto(DClosedTransaction dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "billingPriceBillingPriceId", source = "billingPrice.billingPriceId")
    @Mapping(target = "sellerAppUserId", source = "sellerAppUser.appUserId")
    @Mapping(target = "countryCurrency", source = "company.country.currency")
    @Mapping(target = "status", source = "status", qualifiedByName = "etransactionStatusToEnumResource")
    DClosedTransaction toDto(ClosedTransaction entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "sellerAppUser", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "enumResourceToEtransactionStatus")
    ClosedTransaction merge(DClosedTransaction dto, @MappingTarget ClosedTransaction transaction);

    default List<DClosedTransaction> toList(List<ClosedTransaction> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DClosedTransaction> toPage(Page<ClosedTransaction> page) {
        return page.map(this::toDto);
    }

    @Named("enumResourceToEtransactionStatus")
    default EtransactionStatus enumResourceToEtransactionStatus(EnumResource enumResource) {
        if (enumResource == null || enumResource.getId() == null) {
            return null;
        }
        try {
            return EtransactionStatus.valueOf(enumResource.getId());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("etransactionStatusToEnumResource")
    default EnumResource etransactionStatusToEnumResource(EtransactionStatus status) {
        return status == null ? null : new EnumResource(status.name());
    }
}

