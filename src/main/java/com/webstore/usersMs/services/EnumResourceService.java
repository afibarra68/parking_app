package com.webstore.usersMs.services;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;
import static java.util.ResourceBundle.Control.getNoFallbackControl;
import static java.util.ResourceBundle.getBundle;

/**
 * Servicio genérico para obtener recursos de enums que implementan
 * IEnumResource.
 * Usa reflexión para trabajar con cualquier enum y carga las descripciones
 * desde enum.properties.
 */
@Service
@Log4j2
public class EnumResourceService {

  private static final String ENUM_PROPERTIES_PATH = IEnumResource.DEFAULT_RESOURCE;

  private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("es");

  /**
   * Obtiene la lista de EnumResource para cualquier enum que implemente
   * IEnumResource.
   * Usa reflexión para obtener los valores del enum y carga las descripciones
   * desde enum.properties.
   * 
   * @param enumClass La clase del enum que implementa IEnumResource
   * @return Lista de EnumResource con id, description y descriptionExtended
   * @throws IllegalArgumentException Si la clase no es un enum o no implementa
   *                                  IEnumResource
   */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T> & IEnumResource> List<EnumResource> getEnumResources(Class<T> enumClass) {
    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException("La clase proporcionada no es un enum: " + enumClass.getName());
    }

    if (!IEnumResource.class.isAssignableFrom(enumClass)) {
      throw new IllegalArgumentException(
          "El enum debe implementar IEnumResource: " + enumClass.getName());
    }

    List<EnumResource> resources;

    try {
      Method valuesMethod = enumClass.getMethod("values");
      T[] enumValues = (T[]) valuesMethod.invoke(null);

      ResourceBundle enumBundle = loadEnumPropertiesBundle();

      // Procesar cada valor del enum usando streams
      resources = Stream.of(enumValues)
          .map(enumValue -> {
            String enumKey = enumValue.getEnumKey();
            String resourceKey = enumValue.getResourceKey();
            String description = getDescriptionFromProperties(enumBundle, resourceKey, enumValue);
            // Crear EnumResource con id y description desde properties
            EnumResource enumResource = new EnumResource(enumKey, description);
            // Establecer descriptionExtended con el mismo valor mapeado desde properties
            enumResource.setDescriptionExtended(description);
            return enumResource;
          })
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Error al obtener recursos del enum {} usando reflexión", enumClass.getName(), e);
      throw new RuntimeException("Error al procesar enum: " + enumClass.getName(), e);
    }

    return resources;
  }

  /**
   * Carga el ResourceBundle para enum.properties.
   * 
   * @return ResourceBundle cargado desde enum.properties
   */
  private ResourceBundle loadEnumPropertiesBundle() {
    try {
      return getBundle(ENUM_PROPERTIES_PATH, DEFAULT_LOCALE, getNoFallbackControl(FORMAT_PROPERTIES));
    } catch (Exception e) {
      log.warn("No se pudo cargar enum.properties, se usarán valores por defecto", e);
      return null;
    }
  }

  /**
   * Obtiene la descripción desde enum.properties usando la clave del recurso.
   * Si no se encuentra en el properties, intenta usar getLocaleMessage() del
   * enum.
   * 
   * @param enumBundle  El ResourceBundle cargado desde enum.properties
   * @param resourceKey La clave del recurso (ej: "ETIPOVEHICULO.AUTOMOVIL")
   * @param enumValue   El valor del enum que implementa IEnumResource
   * @return La descripción obtenida desde properties o el nombre del enum como
   *         fallback
   */
  private <T extends Enum<T> & IEnumResource> String getDescriptionFromProperties(
      ResourceBundle enumBundle, String resourceKey, T enumValue) {

    // Intentar obtener desde enum.properties
    if (enumBundle != null) {
      try {
        PropertyResourceBundle propertyBundle = (PropertyResourceBundle) enumBundle;
        if (nonNull(propertyBundle.handleGetObject(resourceKey))) {
          String description = enumBundle.getString(resourceKey);
          if (description != null && !description.trim().isEmpty()) {
            return description.trim();
          }
        }
      } catch (Exception e) {
        log.debug("No se encontró la clave {} en enum.properties", resourceKey);
      }
    }

    // Fallback: intentar usar getLocaleMessage() del enum
    // Pero primero necesitamos crear una versión que use enum.properties
    try {
      // Intentar obtener usando el método getLocaleMessageImpl con enum.properties
      String description = getDescriptionFromEnumResource(enumValue, resourceKey);
      if (description != null && !description.endsWith("_NOT_FOUND")) {
        return description;
      }
    } catch (Exception e) {
      log.debug("No se pudo obtener descripción usando getLocaleMessage para {}", resourceKey);
    }

    // Último fallback: usar el nombre del enum
    return enumValue.name();
  }

  /**
   * Obtiene la descripción usando el ResourceBundle de enum.properties.
   * 
   * @param enumValue   El valor del enum
   * @param resourceKey La clave del recurso
   * @return La descripción obtenida
   */
  private <T extends Enum<T> & IEnumResource> String getDescriptionFromEnumResource(
      T enumValue, String resourceKey) {
    try {
      // Crear un ResourceBundle temporal para enum.properties
      ResourceBundle enumBundle = getBundle(ENUM_PROPERTIES_PATH, DEFAULT_LOCALE,
          getNoFallbackControl(FORMAT_PROPERTIES));

      // Intentar obtener directamente desde el bundle
      if (enumBundle != null && enumBundle.containsKey(resourceKey)) {
        return enumBundle.getString(resourceKey).trim();
      }

    } catch (Exception e) {
      log.debug("Error al obtener descripción usando reflexión para {}", resourceKey, e);
    }

    return null;
  }
}
