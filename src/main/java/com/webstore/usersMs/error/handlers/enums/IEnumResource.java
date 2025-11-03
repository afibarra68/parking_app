package com.webstore.usersMs.error.handlers.enums;

import static java.util.Objects.nonNull;
import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;
import static java.util.ResourceBundle.Control.getNoFallbackControl;
import static java.util.ResourceBundle.getBundle;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.logging.log4j.Level.WARN;
import static org.apache.logging.log4j.message.ParameterizedMessage.format;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public interface IEnumResource {

   String DEFAULT_RESOURCE = "src/main/resources/exceptions/exception.properties";

    String getEnumKey();

    String getResourceKey();

   Logger getLog();

   default String getPropertiesName() {
      return EMPTY;
   }


   default String getLocaleMessage(Object... params) {
      return getLocaleMessageImpl(new Locale("es", ""), params);
   }

   default String getLocaleMessageImpl(Locale locale, Object... params) {
      String resourceKeyName = getResourceKey();
      try {
         String resourceBundleName = StringUtils.isEmpty(getPropertiesName()) ? DEFAULT_RESOURCE : getPropertiesName();
         ResourceBundle rs = getBundle(resourceBundleName, locale, getNoFallbackControl(FORMAT_PROPERTIES));
         if (nonNull(((PropertyResourceBundle) rs).handleGetObject(resourceKeyName))) {
            String message = rs.getString(resourceKeyName);
            return isNotEmpty(params) ? format(message, params) : message;
         }
      } catch (Exception ex) {
         getLog().log(WARN, "Exception loading message for locale: {}", locale.toString());
      }

      return resourceKeyName + "_NOT_FOUND";
   }

}