package com.webstore.usersMs.utils;

import com.webstore.usersMs.error.handlers.enums.EnumResource;

import static java.util.Objects.nonNull;

public class EnumDataMapperUtils {

   /**
    * Mapea un EnumResource a un enum del tipo especificado.
    * 
    * @param clazz     La clase del enum
    * @param enumValue El EnumResource a mapear
    * @return El enum correspondiente o null si enumValue es null
    */
   public static <T extends Enum<T>> T map(Class<T> clazz, EnumResource enumValue) {
      if (enumValue != null && enumValue.getId() != null) {
         try {
            return Enum.valueOf(clazz, enumValue.getId());
         } catch (IllegalArgumentException e) {
            return null;
         }
      }
      return null;
   }

   /**
    * Mapea un EnumResource a un enum del tipo especificado con un valor por
    * defecto.
    * 
    * @param clazz       La clase del enum
    * @param enumValue   El EnumResource a mapear
    * @param defaultEnum El EnumResource por defecto si enumValue es null
    * @return El enum correspondiente o null
    */
   public static <T extends Enum<T>> T map(Class<T> clazz, EnumResource enumValue, EnumResource defaultEnum) {
      if (nonNull(enumValue) && enumValue.getId() != null) {
         try {
            return Enum.valueOf(clazz, enumValue.getId());
         } catch (IllegalArgumentException e) {
            return null;
         }
      } else if (nonNull(defaultEnum) && defaultEnum.getId() != null) {
         try {
            return Enum.valueOf(clazz, defaultEnum.getId());
         } catch (IllegalArgumentException e) {
            return null;
         }
      }
      return null;
   }

   /**
    * Convierte un EnumResource a String (obtiene el ID).
    * 
    * @param enumValue El EnumResource a convertir
    * @return El ID del EnumResource o null si es null
    */
   public static String mapToString(EnumResource enumValue) {
      if (enumValue != null) {
         return enumValue.getId();
      }
      return null;
   }

   /**
    * Convierte un enum a EnumResource.
    * 
    * @param enumValue El enum a convertir
    * @return El EnumResource correspondiente o null si enumValue es null
    */
   public static <T extends Enum<T>> EnumResource mapToEnumResource(T enumValue) {
      if (enumValue != null) {
         return new EnumResource(enumValue.name());
      }
      return null;
   }
}
