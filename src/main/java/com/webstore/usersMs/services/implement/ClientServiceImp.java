package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.entities.Client;
import com.webstore.usersMs.mappers.ClientMapper;
import com.webstore.usersMs.repositories.ClientRepository;
import com.webstore.usersMs.services.ClientService;
import com.webstore.usersMs.error.WbException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClientServiceImp implements ClientService {

    private final ClientRepository repository;

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Override
    public DClient create(DClient client) throws WbException {
        return mapper.toDto(repository.save(mapper.fromDto(client)));
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

}
