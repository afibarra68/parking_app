package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DBusinessService;
import com.webstore.usersMs.entities.BusinessService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.BusinessServiceMapper;
import com.webstore.usersMs.repositories.BusinessServiceRepository;
import com.webstore.usersMs.services.BusinessServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusinessServiceImp implements BusinessServiceInterface {

    private final BusinessServiceRepository repository;

    private final BusinessServiceMapper mapper = Mappers.getMapper(BusinessServiceMapper.class);

    @Override
    public DBusinessService create(DBusinessService dto) throws WbException {
        BusinessService entity = mapper.fromDto(dto);
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DBusinessService update(DBusinessService dto) throws WbException {
        Optional<BusinessService> entityOptional = repository.findByBusinessServiceId(dto.getBusinessServiceId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        BusinessService dbBusinessService = entityOptional.get();
        mapper.merge(dto, dbBusinessService);
        return mapper.toDto(repository.save(dbBusinessService));
    }

    @Override
    public List<DBusinessService> getBy(Long businessServiceId, String principalName, String code) throws WbException {
        List<BusinessService> data = repository.findBy(businessServiceId, principalName, code);
        return mapper.toList(data);
    }

    @Override
    public Page<DBusinessService> findByPageable(Long businessServiceId, String principalName, String code, Pageable pageable) {
        return repository.findByPageable(businessServiceId, principalName, code, pageable).map(mapper::toDto);
    }
}

