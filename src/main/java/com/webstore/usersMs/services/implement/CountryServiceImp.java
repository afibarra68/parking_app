package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.entities.Country;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.CountryMapper;
import com.webstore.usersMs.repositories.CountryRepository;
import com.webstore.usersMs.services.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

@Service
@Log4j2
@RequiredArgsConstructor
public class CountryServiceImp implements CountryService {

    private final CountryRepository repository;

    private final CountryMapper mapper = Mappers.getMapper(CountryMapper.class);

    @Override
    public DCountry create(DCountry client) throws WbException {
        return mapper.toDto(repository.save(mapper.fromDto(client)));
    }

    @Override
    public DCountry update(DCountry dto) throws WbException {
        Optional<Country> entity = repository.findByCountryId(dto.getCountryId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        Country dbClient = entity.get();
        return mapper.toDto(repository.save(mapper.merge(dto, dbClient)));
    }

    @Override
    public List<DCountry> getBy(Long countryId, String description) throws WbException {
        List<Country> data = repository.findBy(countryId, description);
        return mapper.toList(data);
    }

    @Override
    public Page<DCountry> findByPageable(Long countryId, String description, String name, Pageable pageable) throws WbException {
        Page<Country> page = repository.findByPageable(countryId, description, name, pageable);
        return mapper.toPage(page);
    }

    @Override
    public List<DCountry> findByQueryable(Long countryId, String description, String name) throws WbException {
        List<Country> data = repository.findByQueryable(countryId, description, name);
        return mapper.toList(data);
    }

}
