package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.entities.TicketTemplate;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.TicketTemplateMapper;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.TicketTemplateRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.TicketTemplateInterface;
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
public class TicketTemplateImp implements TicketTemplateInterface {

    private final TicketTemplateRepository repository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final TicketTemplateMapper mapper = Mappers.getMapper(TicketTemplateMapper.class);

    @Override
    public DTicketTemplate create(DTicketTemplate dto) throws WbException {
        TicketTemplate entity = mapper.fromDto(dto);
        
        // Set relationships
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                entity.setCompany(company.get());
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                entity.setUser(user.get());
            }
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DTicketTemplate update(DTicketTemplate dto) throws WbException {
        Optional<TicketTemplate> entityOptional = repository.findByTicketTemplateId(dto.getTicketTemplateId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        TicketTemplate dbTicketTemplate = entityOptional.get();
        mapper.merge(dto, dbTicketTemplate);
        
        // Update relationships
        if (dto.getCompanyCompanyId() != null) {
            Optional<Company> company = companyRepository.findByCompanyId(dto.getCompanyCompanyId());
            if (company.isPresent()) {
                dbTicketTemplate.setCompany(company.get());
            }
        }
        
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                dbTicketTemplate.setUser(user.get());
            }
        }
        
        dbTicketTemplate.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(dbTicketTemplate));
    }

    @Override
    public List<DTicketTemplate> getBy(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId) throws WbException {
        List<TicketTemplate> data = repository.findBy(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId);
        return mapper.toList(data);
    }

    @Override
    public Page<DTicketTemplate> findByPageable(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId, Pageable pageable) {
        return repository.findByPageable(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId, pageable).map(mapper::toDto);
    }
}

