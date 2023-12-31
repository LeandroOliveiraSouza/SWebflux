package com.devsuperior.workshopmongo.services;

import com.devsuperior.workshopmongo.dto.UserDTO;
import com.devsuperior.workshopmongo.entities.User;
import com.devsuperior.workshopmongo.repositories.UserRepository;
import com.devsuperior.workshopmongo.services.exceptioons.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public Flux<UserDTO> findAll() {
        return repository.findAll().map(x -> new UserDTO(x));
    }

    public Mono<UserDTO> findById(String id) {
        return repository.findById(id)
                .map(x -> new UserDTO(x))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
    }

    public Mono<UserDTO> insert(UserDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        Mono<UserDTO> result = repository.save(entity).map(x -> new UserDTO(x));
        return result;
    }

    public Mono<UserDTO> update(String id, UserDTO dto) {
        return repository.findById(id)
                .flatMap(x -> {
                    x.setName(dto.getName());
                    x.setEmail(dto.getEmail());
                    return repository.save(x);
                }).map(x -> new UserDTO(x))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
    }

    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")))
                .flatMap(x -> repository.delete(x));
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
    }
}
