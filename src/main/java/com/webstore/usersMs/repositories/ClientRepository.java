package com.webstore.usersMs.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {

    Client save(Client client);

    Optional<Client> findByClientId(Long clientId);

    List<Client> findClientByNumberIdentity(String document);
}
