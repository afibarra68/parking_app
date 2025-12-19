package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DUserPrinter;
import com.webstore.usersMs.entities.Printer;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.UserPrinter;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.mappers.UserPrinterMapper;
import com.webstore.usersMs.repositories.PrinterRepository;
import com.webstore.usersMs.repositories.UserPrinterRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.UserPrinterInterface;
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
public class UserPrinterImp implements UserPrinterInterface {

    private final UserPrinterRepository repository;
    private final UserRepository userRepository;
    private final PrinterRepository printerRepository;

    private final UserPrinterMapper mapper = Mappers.getMapper(UserPrinterMapper.class);

    @Override
    public DUserPrinter create(DUserPrinter dto) throws WbException {
        // Verificar si ya existe la relación
        if (dto.getUserUserId() != null && dto.getPrinterPrinterId() != null) {
            Optional<UserPrinter> existing = repository.findByUserAppUserIdAndPrinterPrinterId(
                dto.getUserUserId(), dto.getPrinterPrinterId());
            if (existing.isPresent()) {
                throw new WbException(CLIENT_NOT_FOUND, "La relación usuario-impresora ya existe");
            }
        }

        UserPrinter entity = mapper.fromDto(dto);
        
        // Set relationships
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                entity.setUser(user.get());
            } else {
                throw new WbException(CLIENT_NOT_FOUND, "Usuario no encontrado");
            }
        }
        
        if (dto.getPrinterPrinterId() != null) {
            Optional<Printer> printer = printerRepository.findByPrinterId(dto.getPrinterPrinterId());
            if (printer.isPresent()) {
                entity.setPrinter(printer.get());
            } else {
                throw new WbException(CLIENT_NOT_FOUND, "Impresora no encontrada");
            }
        }
        
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());
        
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DUserPrinter update(DUserPrinter dto) throws WbException {
        Optional<UserPrinter> entityOptional = repository.findByUserPrinterId(dto.getUserPrinterId());
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        UserPrinter dbUserPrinter = entityOptional.get();
        mapper.merge(dto, dbUserPrinter);
        
        // Update relationships
        if (dto.getUserUserId() != null) {
            Optional<User> user = userRepository.findByAppUserId(dto.getUserUserId());
            if (user.isPresent()) {
                dbUserPrinter.setUser(user.get());
            }
        }
        
        if (dto.getPrinterPrinterId() != null) {
            Optional<Printer> printer = printerRepository.findByPrinterId(dto.getPrinterPrinterId());
            if (printer.isPresent()) {
                dbUserPrinter.setPrinter(printer.get());
            }
        }
        
        dbUserPrinter.setUpdatedDate(LocalDateTime.now());
        
        return mapper.toDto(repository.save(dbUserPrinter));
    }

    @Override
    public List<DUserPrinter> getBy(Long userPrinterId, Long userUserId, Long printerPrinterId, Boolean isActive) throws WbException {
        List<UserPrinter> data = repository.findBy(userPrinterId, userUserId, printerPrinterId, isActive);
        return mapper.toList(data);
    }

    @Override
    public Page<DUserPrinter> findByPageable(Long userPrinterId, Long userUserId, Long printerPrinterId, Boolean isActive, Pageable pageable) {
        return repository.findByPageable(userPrinterId, userUserId, printerPrinterId, isActive, pageable).map(mapper::toDto);
    }

    @Override
    public void delete(Long userPrinterId) throws WbException {
        Optional<UserPrinter> entityOptional = repository.findByUserPrinterId(userPrinterId);
        if (entityOptional.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        repository.delete(entityOptional.get());
    }
}

