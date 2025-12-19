package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DUserPrinterType;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.UserPrinterType;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.UserPrinterTypeMapper;
import com.webstore.usersMs.repositories.UserPrinterTypeRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.UserPrinterTypeInterface;
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
public class UserPrinterTypeImp implements UserPrinterTypeInterface {

    private final UserPrinterTypeRepository repository;
    private final UserRepository userRepository;

    private final UserPrinterTypeMapper mapper = Mappers.getMapper(UserPrinterTypeMapper.class);

    @Override
    public DUserPrinterType create(DUserPrinterType dto) throws WbException {
        // Verificar si ya existe la relación
        if (dto.getUserUserId() != null && dto.getPrinterType() != null) {
            Optional<UserPrinterType> existing = repository.findByUserAppUserIdAndPrinterType(
                dto.getUserUserId(), dto.getPrinterType());
            if (existing.isPresent()) {
                throw new WbException(CLIENT_NOT_FOUND, "El tipo de impresora ya está habilitado para este usuario");
            }
        }

        UserPrinterType entity = mapper.fromDto(dto);
        
        // Set relationships
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                entity.setUser(user.get());
            } else {
                throw new WbException(CLIENT_NOT_FOUND, "Usuario no encontrado");
            }
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());
        
        if (entity.getIsEnabled() == null) {
            entity.setIsEnabled(true);
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DUserPrinterType update(DUserPrinterType dto) throws WbException {
        Optional<UserPrinterType> entityOptional = repository.findByUserPrinterTypeId(dto.getUserPrinterTypeId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        UserPrinterType dbUserPrinterType = entityOptional.get();
        mapper.merge(dto, dbUserPrinterType);
        
        // Update relationships
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                dbUserPrinterType.setUser(user.get());
            }
        }
        
        dbUserPrinterType.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(dbUserPrinterType));
    }

    @Override
    public List<DUserPrinterType> getBy(Long userPrinterTypeId, Long userUserId, String printerType, Boolean isEnabled) throws WbException {
        List<UserPrinterType> data = repository.findBy(userPrinterTypeId, userUserId, printerType, isEnabled);
        return mapper.toList(data);
    }

    @Override
    public Page<DUserPrinterType> findByPageable(Long userPrinterTypeId, Long userUserId, String printerType, Boolean isEnabled, Pageable pageable) {
        return repository.findByPageable(userPrinterTypeId, userUserId, printerType, isEnabled, pageable).map(mapper::toDto);
    }

    @Override
    public void delete(Long userPrinterTypeId) throws WbException {
        Optional<UserPrinterType> entityOptional = repository.findByUserPrinterTypeId(userPrinterTypeId);
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        repository.delete(entityOptional.get());
    }
}

