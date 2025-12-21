package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.webstore.usersMs.dtos.DMapField;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.entities.OpenTransaction;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.entities.enums.EtransactionStatus;
import com.webstore.usersMs.utils.EnumDataMapper;
import com.webstore.usersMs.utils.EnumDataMapperUtils;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE, imports = { EnumDataMapperUtils.class, ETipoVehiculo.class,
        EtransactionStatus.class, EnumDataMapper.class }, uses = { EnumDataMapper.class })
public interface OpenTransactionMapper {

    String STATUS_EXP = "java(EnumDataMapperUtils.map(EtransactionStatus.class, dto.getStatus()))";

    String TIPO_VEHICULO_EXP = "java(EnumDataMapperUtils.map(ETipoVehiculo.class, dto.getTipoVehiculo()))";

    String GET_STATUS_EXP = "java(EnumDataMapper.fromDto(entity.getStatus()))";

    String GET_TIPO_VEHICULO_EXP = "java(EnumDataMapper.fromDto(entity.getTipoVehiculo()))";

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    @Mapping(target = "status", expression = STATUS_EXP, resultType = EtransactionStatus.class)
    @Mapping(target = "tipoVehiculo", expression = TIPO_VEHICULO_EXP, resultType = ETipoVehiculo.class)
    OpenTransaction fromDto(DOpenTransaction dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "billingPriceBillingPriceId", source = "billingPrice.billingPriceId")
    @Mapping(target = "appUserAppUserSeller", source = "appUserSeller.appUserId")
    @Mapping(target = "status", expression = GET_STATUS_EXP)
    @Mapping(target = "tipoVehiculo", expression = GET_TIPO_VEHICULO_EXP)
    DOpenTransaction toDto(OpenTransaction entity);

    @Mapping(target = "status", source = "status.description")
    DMapField toPrinter(DOpenTransaction entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "billingPrice", ignore = true)
    @Mapping(target = "appUserSeller", ignore = true)
    @Mapping(target = "status", expression = STATUS_EXP, resultType = EtransactionStatus.class)
    @Mapping(target = "tipoVehiculo", expression = TIPO_VEHICULO_EXP, resultType = ETipoVehiculo.class)
    OpenTransaction merge(DOpenTransaction dto, @MappingTarget OpenTransaction transaction);

    default List<DOpenTransaction> toList(List<OpenTransaction> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DOpenTransaction> toPage(Page<OpenTransaction> page) {
        return page.map(this::toDto);
    }
}
