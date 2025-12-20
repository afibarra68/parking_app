package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.utils.EnumDataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.utils.EnumDataMapperUtils;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE, imports = { EnumDataMapperUtils.class, ETipoVehiculo.class,
        EBillingStatus.class, EnumDataMapper.class }, uses = { EnumDataMapper.class} )
public interface BillingPriceMapper {

    String STATUS_EXP = "java(EnumDataMapperUtils.map(EBillingStatus.class, dto.getStatus()))";

    String TIPO_VEHICULO_EXP = "java(EnumDataMapperUtils.map(ETipoVehiculo.class, dto.getTipoVehiculo()))";

    String GET_STATUS_EXP = "java(EnumDataMapper.fromDto(entity.getStatus()))";

    String GET_TIPO_VEHICULO_EXP = "java(EnumDataMapper.fromDto(entity.getTipoVehiculo()))";

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "businessService", ignore = true)
    @Mapping(target = "status", expression = STATUS_EXP, resultType = EBillingStatus.class)
    @Mapping(target = "tipoVehiculo", expression = TIPO_VEHICULO_EXP, resultType = ETipoVehiculo.class)
    BillingPrice fromDto(DBillingPrice dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "discountDiscountId", source = "discount.discountId")
    @Mapping(target = "businessServiceBusinessServiceId", source = "businessService.businessServiceId")
    @Mapping(target = "status", expression = GET_STATUS_EXP)
    @Mapping(target = "tipoVehiculo", expression = GET_TIPO_VEHICULO_EXP)
    DBillingPrice toDto(BillingPrice entity);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "businessService", ignore = true)
    @Mapping(target = "status", expression = STATUS_EXP, resultType = EBillingStatus.class)
    @Mapping(target = "tipoVehiculo", expression = TIPO_VEHICULO_EXP, resultType = ETipoVehiculo.class)
    BillingPrice merge(DBillingPrice dto, @MappingTarget BillingPrice billingPrice);

    default List<DBillingPrice> toList(List<BillingPrice> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DBillingPrice> toPage(Page<BillingPrice> page) {
        return page.map(this::toDto);
    }
}
