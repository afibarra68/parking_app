package com.webstore.usersMs.utils;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnumDataMapper {

   public static EnumResource fromDto(IEnumResource iData) {
      if (iData != null) {
         EnumResource data = new EnumResource();
         String description = iData.getLocaleMessage();
         data.setDescription(description);
         data.setDescriptionExtended(description);
         data.setId(iData.getEnumKey());
         return data;
      } else {
         return null;
      }
   }

}
