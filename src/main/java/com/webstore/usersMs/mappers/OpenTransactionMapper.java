package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.webstore.usersMs.dtos.DMapField;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.entities.OpenTransaction;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.entities.enums.EtransactionStatus;
import com.webstore.usersMs.error.handlers.enums.EnumResource;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface OpenTransactionMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    @Mapping(target = "tipoVehiculo", source = "tipoVehiculo", qualifiedByName = "stringToTipoVehiculo")
    @Mapping(target = "status", source = "status", qualifiedByName = "enumResourceToEtransactionStatus")
    OpenTransaction fromDto(DOpenTransaction dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "billingPriceBillingPriceId", source = "billingPrice.billingPriceId")
    @Mapping(target = "appUserAppUserSeller", source = "appUserSeller.appUserId")
    @Mapping(target = "tipoVehiculo", source = "tipoVehiculo", qualifiedByName = "tipoVehiculoToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "etransactionStatusToEnumResource")
    DOpenTransaction toDto(OpenTransaction entity);


    @Mapping(target = "status", source = "status.description")
    DMapField toPrinter(DOpenTransaction entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    @Mapping(target = "tipoVehiculo", source = "tipoVehiculo", qualifiedByName = "stringToTipoVehiculo")
    @Mapping(target = "status", source = "status", qualifiedByName = "enumResourceToEtransactionStatus")
    OpenTransaction merge(DOpenTransaction dto, @MappingTarget OpenTransaction transaction);

    default List<DOpenTransaction> toList(List<OpenTransaction> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DOpenTransaction> toPage(Page<OpenTransaction> page) {
        return page.map(this::toDto);
    }

    @Named("stringToTipoVehiculo")
    default ETipoVehiculo stringToTipoVehiculo(String tipoVehiculo) {
        if (tipoVehiculo == null || tipoVehiculo.isEmpty()) {
            return null;
        }
        try {
            return ETipoVehiculo.valueOf(tipoVehiculo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("tipoVehiculoToString")
    default String tipoVehiculoToString(ETipoVehiculo tipoVehiculo) {
        return tipoVehiculo == null ? null : tipoVehiculo.name();
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
