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

@Service
@Log4j2
@RequiredArgsConstructor
public class TemplatePrinterServiceImp implements TemplatePrinterService {

    private static final String PRINT_ENDPOINT = "/print";

    private final TicketTemplateInterface ticketTemplateService;

    private final PrinterRepository printerRepository;

    private final UserService userService;

    private final WebClient webClient;

    @Override
    public String buildTicket(EReceiptModel eReceiptModel, DMapField fieldSet, DTicketTemplate template) throws WbException {
        return null;
    }

}
