package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DMapField;
import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.OpenTransaction;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.BillingPrice;
import com.webstore.usersMs.entities.Printer;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.repositories.OpenTransactionRepository;
import com.webstore.usersMs.repositories.PrinterRepository;
import com.webstore.usersMs.services.TemplatePrinterService;
import com.webstore.usersMs.services.TicketTemplateInterface;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.utils.PrintConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.webstore.usersMs.entities.enums.EReceiptModel.IN;

@Service
@Log4j2
@RequiredArgsConstructor
public class TemplatePrinterServiceImp implements TemplatePrinterService {

    private static final String PRINT_ENDPOINT = "/print";

    @Override
    public String buildTicket(EReceiptModel eReceiptModel, DMapField fieldSet, DTicketTemplate template) throws WbException {
        HashMap<String, String> inOut;
        switch (eReceiptModel) {
            case IN:
                inOut = generateKeyValueOnIn(fieldSet, template);
                break;
            case OUT:
                inOut = generateKeyValueOnOut(fieldSet, template);
                break;
            case LIQUID:
                log.info("//:todo_for_implement");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + eReceiptModel);
        }

        return null;
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
        drt.put(PrintConstants.TIME_ELAPSED, fieldSet.getTimeElapsed());;
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

        //todo:  more sets
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
        drt.put(PrintConstants.TIME_ELAPSED, fieldSet.getTimeElapsed());;
        drt.put(PrintConstants.OPERATION_DATE, fieldSet.getOperationDate());
        drt.put(PrintConstants.SERVICE_ID, fieldSet.getServiceId());
        drt.put(PrintConstants.SELLER_NAME, fieldSet.getSellerName());
        drt.put(PrintConstants.VEHICLE_PLATE, fieldSet.getVehiclePlate());
        drt.put(PrintConstants.VEHICULE_TYPE, fieldSet.getVehiculeType());
        drt.put(PrintConstants.PRINTER_TYPE, fieldSet.getPrinterType());

        return drt;
    }

}
