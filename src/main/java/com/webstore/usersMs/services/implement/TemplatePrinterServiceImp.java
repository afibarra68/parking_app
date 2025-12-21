package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DDataPrinting;
import com.webstore.usersMs.dtos.DMapField;
import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.TemplatePrinterService;
import com.webstore.usersMs.utils.PrintConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
@Log4j2
@RequiredArgsConstructor
public class TemplatePrinterServiceImp implements TemplatePrinterService {

    private static final String PLACEHOLDER_START = "{";

    private static final String PLACEHOLDER_END = "}";

    @Override
    public DDataPrinting buildTicket(EReceiptModel eReceiptModel, DMapField fieldSet, DTicketTemplate template)
            throws WbException {
        HashMap<String, String> inOut;
        switch (eReceiptModel) {
            case IN:
                inOut = generateKeyValueOnIn(fieldSet, template);
                return getDataPrinting(inOut, template);
            case OUT:
                inOut = generateKeyValueOnOut(fieldSet, template);
                return getDataPrinting(inOut, template);
            default:
                throw new IllegalStateException("Unexpected value: " + eReceiptModel);
        }
    }

    private HashMap<String, String> generateKeyValueOnOut(DMapField fieldSet, DTicketTemplate template) {
        HashMap<String, String> drt = new HashMap<>();
        drt.put(PrintConstants.COMPANY_DESCRIPTION, fieldSet.getCompanyDescription());
        drt.put(PrintConstants.NIT, fieldSet.getNit());
        drt.put(PrintConstants.START_DAY, fieldSet.getStartDay());
        drt.put(PrintConstants.START_TIME, fieldSet.getStartTime());
        drt.put(PrintConstants.END_DATE, fieldSet.getEndDate());
        drt.put(PrintConstants.END_TIME, fieldSet.getEndTime());
        drt.put(PrintConstants.CURRENCY, fieldSet.getCurrency());
        drt.put(PrintConstants.COMPANY_ID, fieldSet.getCompanyId());
        drt.put(PrintConstants.IDENTITY, fieldSet.getIdentity());
        drt.put(PrintConstants.STATUS, fieldSet.getStatus());
        drt.put(PrintConstants.BILLING_PRICE_ID, fieldSet.getBillingPriceId());
        drt.put(PrintConstants.AMOUNT, fieldSet.getAmount());
        drt.put(PrintConstants.DISCOUNT, fieldSet.getDiscount());
        drt.put(PrintConstants.TOTAL_AMOUNT, fieldSet.getTotalAmount());
        drt.put(PrintConstants.TIME_ELAPSED, fieldSet.getTimeElapsed());
        drt.put(PrintConstants.OPERATION_DATE, fieldSet.getOperationDate());
        drt.put(PrintConstants.SERVICE_ID, fieldSet.getServiceId());
        drt.put(PrintConstants.SELLER_NAME, fieldSet.getSellerName());
        drt.put(PrintConstants.VEHICLE_PLATE, fieldSet.getVehiclePlate());
        drt.put(PrintConstants.VEHICULE_TYPE, fieldSet.getVehiculeType());
        drt.put(PrintConstants.PRINTER_TYPE, fieldSet.getPrinterType());
        return drt;
    }

    private HashMap<String, String> generateKeyValueOnIn(DMapField fieldSet, DTicketTemplate template) {
        HashMap<String, String> drt = new HashMap<>();

        // todo: more sets
        drt.put(PrintConstants.START_DAY, fieldSet.getStartDay());
        drt.put(PrintConstants.START_TIME, fieldSet.getStartTime());
        drt.put(PrintConstants.END_DATE, fieldSet.getEndDate());
        drt.put(PrintConstants.END_TIME, fieldSet.getEndTime());
        drt.put(PrintConstants.CURRENCY, fieldSet.getCurrency());
        drt.put(PrintConstants.COMPANY_ID, fieldSet.getCompanyId());
        drt.put(PrintConstants.COMPANY_DESCRIPTION, fieldSet.getCompanyDescription());
        drt.put(PrintConstants.NIT, fieldSet.getNit());
        drt.put(PrintConstants.IDENTITY, fieldSet.getIdentity());
        drt.put(PrintConstants.STATUS, fieldSet.getStatus());
        drt.put(PrintConstants.BILLING_PRICE_ID, fieldSet.getBillingPriceId());
        drt.put(PrintConstants.AMOUNT, fieldSet.getAmount());
        drt.put(PrintConstants.DISCOUNT, fieldSet.getDiscount());
        drt.put(PrintConstants.TOTAL_AMOUNT, fieldSet.getTotalAmount());
        drt.put(PrintConstants.TIME_ELAPSED, fieldSet.getTimeElapsed());
        drt.put(PrintConstants.OPERATION_DATE, fieldSet.getOperationDate());
        drt.put(PrintConstants.SERVICE_ID, fieldSet.getServiceId());
        drt.put(PrintConstants.SELLER_NAME, fieldSet.getSellerName());
        drt.put(PrintConstants.VEHICLE_PLATE, fieldSet.getVehiclePlate());
        drt.put(PrintConstants.VEHICULE_TYPE, fieldSet.getVehiculeType());
        drt.put(PrintConstants.PRINTER_TYPE, fieldSet.getPrinterType());

        return drt;
    }

    private DDataPrinting getDataPrinting(HashMap<String, String> fieldSet, DTicketTemplate dataTemplate) {
        String fieldTemplate = interpolateFields(fieldSet, dataTemplate.getTemplate());

        return DDataPrinting.builder()
                .template(fieldTemplate)
                .printerName(dataTemplate.getPrinterName())
                .printerType(dataTemplate.getPrinterType())
                .conectionString(dataTemplate.getConectionString())
                .build();
    }

    /**
     * Interpola los campos del template reemplazando placeholders con valores del
     * HashMap.
     * Busca todos los placeholders en formato {campo} y los reemplaza con los
     * valores correspondientes.
     * Similar al estilo de HtmlParser.parseJsoup().
     * 
     * @param fieldSet       HashMap con los valores a reemplazar (claves de
     *                       PrintConstants)
     * @param bufferTemplate Template con placeholders en formato {campo}
     * @return Template con placeholders reemplazados por valores
     */
    /**
     * Interpola los campos del template reemplazando placeholders con valores del
     * HashMap.
     * Busca todos los placeholders en formato {campo} y los reemplaza con los
     * valores correspondientes del HashMap usando las claves de PrintConstants.
     * Similar al estilo de HtmlParser.parseJsoup().
     * 
     * @param fieldSet       HashMap con los valores a reemplazar (claves de
     *                       PrintConstants)
     * @param bufferTemplate Template con placeholders en formato {campo}
     * @return Template con placeholders reemplazados por valores
     */
    private String interpolateFields(HashMap<String, String> fieldSet, String bufferTemplate) {
        if (bufferTemplate == null || bufferTemplate.isEmpty()) {
            return bufferTemplate;
        }

        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int templateLength = bufferTemplate.length();

        while (startIndex < templateLength) {
            int placeholderStart = bufferTemplate.indexOf(PLACEHOLDER_START, startIndex);

            if (placeholderStart == -1) {
                // No hay más placeholders, agregar el resto del template
                result.append(bufferTemplate.substring(startIndex));
                break;
            }

            // Agregar el texto antes del placeholder
            result.append(bufferTemplate.substring(startIndex, placeholderStart));

            // Buscar el cierre del placeholder
            int placeholderEnd = bufferTemplate.indexOf(PLACEHOLDER_END, placeholderStart);
            if (placeholderEnd == -1) {
                // Placeholder sin cerrar, agregar el resto y terminar
                result.append(bufferTemplate.substring(placeholderStart));
                break;
            }

            // Extraer el nombre del placeholder (sin llaves) - debe coincidir con
            // PrintConstants
            String placeholderKey = bufferTemplate.substring(placeholderStart + 1, placeholderEnd);
            String replacement = findReplacement(placeholderKey, fieldSet);

            result.append(replacement);
            startIndex = placeholderEnd + 1;
        }

        return result.toString();
    }

    /**
     * Encuentra el valor de reemplazo para un placeholder buscando directamente
     * en el HashMap usando la clave de PrintConstants.
     * 
     * @param placeholderKey Clave del placeholder (sin llaves) - debe coincidir
     *                       con PrintConstants
     * @param fieldSet       HashMap con los valores (claves de PrintConstants)
     * @return Valor de reemplazo o cadena vacía si no se encuentra
     */
    private String findReplacement(String placeholderKey, HashMap<String, String> fieldSet) {
        if (placeholderKey == null || placeholderKey.isEmpty()) {
            return "";
        }

        // Buscar directamente en el HashMap usando la clave del placeholder
        String value = fieldSet.get(placeholderKey);
        if (value != null) {
            return value;
        }

        // Si no se encuentra, retornar cadena vacía y registrar warning
        log.warn("Placeholder '{}' no encontrado en fieldSet", placeholderKey);
        return "";
    }
}
