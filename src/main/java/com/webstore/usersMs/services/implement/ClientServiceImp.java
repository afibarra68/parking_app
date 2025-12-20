package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.entities.Client;
import com.webstore.usersMs.mappers.ClientMapper;
import com.webstore.usersMs.repositories.ClientRepository;
import com.webstore.usersMs.services.ClientService;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.error.WbException;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClientServiceImp implements ClientService {

    private final ClientRepository repository;

    private final UserService userService;

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Override
    public DClient create(DClient client) throws WbException {
        Client entity = mapper.fromDto(client);
        
        // Obtener el usuario autenticado y asignar la compañía automáticamente
        UserLogin authenticatedUser = userService.getAuthenticatedUser();
        if (authenticatedUser != null && authenticatedUser.getCompanyId() != null) {
            // Si no viene clientCompanyId en el DTO, asignarlo desde el usuario autenticado
            if (client.getClientCompanyId() == null) {
                entity.setClientCompanyId(authenticatedUser.getCompanyId());
                log.info("clientCompanyId asignado automáticamente desde la sesión: {}", authenticatedUser.getCompanyId());
            }
        }
        
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public DClient update(DClient dto) throws WbException {
        Optional<Client> entity =  repository.findByClientId(dto.getClientId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        Client dbClient = entity.get();
        return mapper.toDto(repository.save(mapper.merge(dto, dbClient )));
    }

    @Override
    public Page<DClient> findBy(String document, Pageable pageable) {
        return mapper.toPage(repository.findBy(document, pageable));
    }

}
