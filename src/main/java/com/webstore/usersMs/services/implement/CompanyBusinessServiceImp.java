package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DCompanyBusinessService;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.CompanyBusinessService;
import com.webstore.usersMs.entities.BusinessService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.CompanyBusinessServiceMapper;
import com.webstore.usersMs.repositories.CompanyBusinessServiceRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.BusinessServiceRepository;
import com.webstore.usersMs.services.CompanyBusinessServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

@Service
@Log4j2
@RequiredArgsConstructor
public class CompanyBusinessServiceImp implements CompanyBusinessServiceInterface {

    private final CompanyBusinessServiceRepository repository;
    private final CompanyRepository companyRepository;
    private final BusinessServiceRepository businessServiceRepository;

    private final CompanyBusinessServiceMapper mapper = Mappers.getMapper(CompanyBusinessServiceMapper.class);

    @Override
    public DCompanyBusinessService create(DCompanyBusinessService dto) throws WbException {
        CompanyBusinessService entity = mapper.fromDto(dto);
        
        // Cargar Company si se proporciona
        if (dto.getCompany() != null && dto.getCompany().getCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompany().getCompanyId());
            if (company.isEmpty()) {
                throw new WbException(CLIENT_NOT_FOUND);
            }
            entity.setCompany(company.get());
        }
        
        // Cargar BusinessService si se proporciona
        if (dto.getBusinessService() != null && dto.getBusinessService().getBusinessServiceId() != null) {
            Optional<BusinessService> businessService = businessServiceRepository.findByBusinessServiceId(dto.getBusinessService().getBusinessServiceId());
            if (businessService.isEmpty()) {
                throw new WbException(CLIENT_NOT_FOUND);
            }
            entity.setBusinessService(businessService.get());
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DCompanyBusinessService update(DCompanyBusinessService dto) throws WbException {
        Optional<CompanyBusinessService> entityOptional = repository.findByCompanyBusinessServiceId(dto.getCompanyBusinessServiceId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        CompanyBusinessService dbCompanyBusinessService = entityOptional.get();
        mapper.merge(dto, dbCompanyBusinessService);
        
        // Actualizar Company si se proporciona
        if (dto.getCompany() != null && dto.getCompany().getCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompany().getCompanyId());
            if (company.isPresent()) {
                dbCompanyBusinessService.setCompany(company.get());
            }
        }
        
        // Actualizar BusinessService si se proporciona
        if (dto.getBusinessService() != null && dto.getBusinessService().getBusinessServiceId() != null) {
            Optional<BusinessService> businessService = businessServiceRepository.findByBusinessServiceId(dto.getBusinessService().getBusinessServiceId());
            if (businessService.isPresent()) {
                dbCompanyBusinessService.setBusinessService(businessService.get());
            }
        }
        
        return mapper.toDto(repository.save(dbCompanyBusinessService));
    }

    @Override
    public List<DCompanyBusinessService> getBy(Long companyBusinessServiceId, Long companyId, Long businessServiceId) throws WbException {
        List<CompanyBusinessService> data = repository.findBy(companyBusinessServiceId, companyId, businessServiceId);
        return mapper.toList(data);
    }

    @Override
    public List<DCompanyBusinessService> getByCompanyId(Long companyId) {
        log.info("Buscando servicios de negocio para la empresa con ID: {}", companyId);
        List<CompanyBusinessService> data = repository.findByCompanyId(companyId);
        log.info("Se encontraron {} servicios de negocio para la empresa {}", data.size(), companyId);
        return mapper.toList(data);
    }

    @Override
    public void delete(Long companyBusinessServiceId) throws WbException {
        Optional<CompanyBusinessService> entityOptional = repository.findByCompanyBusinessServiceId(companyBusinessServiceId);
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        repository.delete(entityOptional.get());
    }
}

