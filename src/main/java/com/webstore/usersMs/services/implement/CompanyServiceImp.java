package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;
import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACCESS_DENIED_NO_COMPANY;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.Country;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.CompanyMapper;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.CountryRepository;
import com.webstore.usersMs.services.CompanyService;
import com.webstore.usersMs.services.UserService;

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
    private final UserService userService;

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

    @Override
    public Page<DCompany> findByPageable(Long companyId, String companyName, String numberIdentity, Pageable pageable) {
        return repository.findByPageable(companyId, companyName, numberIdentity, pageable).map(mapper::toDto);
    }

    @Override
    public DCompany getCurrentUserCompany() throws WbException {
        // Obtener el usuario autenticado
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            log.error("Intento de obtener empresa sin usuario autenticado.");
            throw new WbException(com.webstore.usersMs.error.handlers.enums.WbErrorCode.USER_NOT_FOUND);
        }

        if (authenticatedUser.getCompanyId() == null) {
            log.warn("Usuario autenticado {} no tiene companyId asociado.", authenticatedUser.getAppUserId());
            throw new WbException(ACCESS_DENIED_NO_COMPANY);
        }

        // Buscar la empresa por companyId
        Optional<Company> companyOpt = repository.findByCompanyId(authenticatedUser.getCompanyId());
        if (companyOpt.isEmpty()) {
            log.warn("No se encontró empresa con ID {} para el usuario autenticado.", authenticatedUser.getCompanyId());
            throw new WbException(CLIENT_NOT_FOUND);
        }

        return mapper.toDto(companyOpt.get());
    }

}

