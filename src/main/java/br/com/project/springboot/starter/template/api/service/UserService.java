package br.com.project.springboot.starter.template.api.service;

import br.com.project.springboot.starter.template.api.entities.User;
import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import br.com.project.springboot.starter.template.api.exceptions.ApiException;
import br.com.project.springboot.starter.template.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return repository.save(user);
    }

    public User findByEmail(String email) throws ApiException {
        return repository.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiMessageEnum.USER_NOT_FOUND));
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailAndIdNot(email, id);
    }
}
