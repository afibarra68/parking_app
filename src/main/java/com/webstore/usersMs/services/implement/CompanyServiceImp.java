package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Country;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.CompanyMapper;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.CountryRepository;
import com.webstore.usersMs.services.CompanyService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CompanyServiceImp implements CompanyService {

    private final CompanyRepository repository;
    private final CountryRepository countryRepository;

    private final CompanyMapper mapper = Mappers.getMapper(CompanyMapper.class);

    @Override
    public DCompany create(DCompany dto) throws WbException {
        Company entity = mapper.fromDto(dto);
        
        // Si el DTO tiene un country con countryId, cargar la entidad Country
        if (dto.getCountry() != null && dto.getCountry().getCountryId() != null) {
            Optional<Country> country = countryRepository.findByCountryId(dto.getCountry().getCountryId());
            if (country.isPresent()) {
                entity.setCountry(country.get());
            }
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DCompany update(DCompany dto) throws WbException {
        Optional<Company> entity = repository.findByCompanyId(dto.getCompanyId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        Company dbCompany = entity.get();
        
        // Actualizar el country si se proporciona
        if (dto.getCountry() != null && dto.getCountry().getCountryId() != null) {
            Optional<Country> country = countryRepository.findByCountryId(dto.getCountry().getCountryId());
            if (country.isPresent()) {
                dbCompany.setCountry(country.get());
            }
        } else if (dto.getCountry() == null) {
            // Si no se proporciona country, mantener el existente o limpiarlo
            dbCompany.setCountry(null);
        }
        
        // Actualizar otros campos
        dbCompany.setCompanyName(dto.getCompanyName());
        dbCompany.setNumberIdentity(dto.getNumberIdentity());
        
        return mapper.toDto(repository.save(dbCompany));
    }

    @Override
    public List<DCompany> getBy(Long companyId, String companyName, String numberIdentity) throws WbException {
        List<Company> data = repository.findBy(companyId, companyName, numberIdentity);
        return mapper.toList(data);
    }

}

