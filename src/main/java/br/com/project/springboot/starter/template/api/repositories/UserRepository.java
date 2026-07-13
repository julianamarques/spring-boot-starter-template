package br.com.project.springboot.starter.template.api.repositories;

import br.com.project.springboot.starter.template.api.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends GenericRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
