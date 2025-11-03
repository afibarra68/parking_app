package com.webstore.usersMs.h2Data;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN;
import static java.util.Arrays.stream;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.repositories.UserRoleRepository;
import java.io.File;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class InsertData {

    public static final String TEST_DATA = "csv";

    public static final String USER_CSV = "/user.csv";

    public static final String ROLE_CSV = "/role.csv";

    private final Environment environment;

    private final UserRepository repository;

    private final UserRoleRepository roleRepository;

    @Async
    public void insertData() {
        loadUser();
    }

    private void loadUser() {
        log.info("Start processing Loading User");
        stream(environment.getActiveProfiles())
            .filter(TEST_DATA::contains)
            .findAny()
            .ifPresent(profile -> generateInitialConfiguration(User.class, TEST_DATA + USER_CSV)
                .stream()
                .filter(rUser -> repository.findByNumberIdentity(rUser.getFirstName()).isEmpty())
                .forEach(r -> {
                    try {
                        repository.save(r);
                    } catch (Exception e) {
                        log.info("Can not be create User {}" , r.getFirstName());
                    }
                }));
        log.info("Finish processing Loading User");
    }


    public <T> List<T> generateInitialConfiguration(Class<T> type, String fileName) {
        try {
            File file = new ClassPathResource(fileName).getFile();
            MappingIterator<T> readValues = CsvMapper
                .builder()
                .configure(IGNORE_UNKNOWN, true)
                .enable(CsvParser.Feature.TRIM_SPACES)
                .build()
                .readerFor(type)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(file);
            return readValues.readAll();
        } catch (Exception ex) {
            log.error("Error occurred while loading object list from file");
        }
        return Collections.emptyList();
    }

}
