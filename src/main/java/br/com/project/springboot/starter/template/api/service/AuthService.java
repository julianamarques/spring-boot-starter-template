package br.com.project.springboot.starter.template.api.service;

import br.com.project.springboot.starter.template.api.dtos.request.AuthRequestDTO;
import br.com.project.springboot.starter.template.api.dtos.request.UserRequestDTO;
import br.com.project.springboot.starter.template.api.dtos.response.UserResponseDTO;
import br.com.project.springboot.starter.template.api.entities.User;
import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import br.com.project.springboot.starter.template.api.enums.RoleEnum;
import br.com.project.springboot.starter.template.api.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String DUMMY_PASSWORD_HASH = "$2a$10$GuJVDKznfY57Kyo6rP17PuONfe6nQe.gcz6qtxyRtVqbnrIAC3rnO";

    private final UserService userService;
    private final TokenService tokenService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO auth(AuthRequestDTO request) throws ApiException {
        User user;

        try {
            user = loadUserByUsername(request.getEmail());
        } catch (UsernameNotFoundException e) {
            passwordEncoder.matches(request.getPassword(), DUMMY_PASSWORD_HASH);
            throw unauthorized();
        }

        try {
            validatePassword(request.getPassword(), user.getPassword());
        } catch (ApiException e) {
            throw unauthorized();
        }

        validateActiveUser(user);

        String token = tokenService.generateToken(user);
        Date expirationDate = tokenService.getExpirationDate(token);

        return new UserResponseDTO(token, expirationDate, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO save(UserRequestDTO request) throws ApiException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessageEnum.INVALID_PASSWORD);
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessageEnum.EMAIL_EXISTS);
        }

        User user = request.convertToEntity(encryptPassword(request.getPassword()));
        user.setRoles(new ArrayList<>(List.of(roleService.findByName(RoleEnum.USER))));
        user = userService.save(user);
        String token = tokenService.generateToken(user);
        Date expirationDate = tokenService.getExpirationDate(token);

        return new UserResponseDTO(token, expirationDate, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO edit(String token, UserRequestDTO request) throws ApiException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessageEnum.INVALID_PASSWORD);
        }

        String rawToken = extractToken(token);
        User authUser = getAuthUser();

        if (userService.existsByEmailAndIdNot(request.getEmail(), authUser.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ApiMessageEnum.EMAIL_EXISTS);
        }

        User user = userService.save(request.convertToEntity(authUser, encryptPassword(request.getPassword())));
        Date expirationDate = tokenService.getExpirationDate(rawToken);

        return new UserResponseDTO(rawToken, expirationDate, user);
    }

    public User getAuthUser() throws ApiException {
        try {
            return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ApiMessageEnum.ACCESS_DENIED);
        }
    }

    public User validateToken(String token) throws ApiException {
        String email = tokenService.getSubject(token);
        User user = userService.findByEmail(email);

        validateActiveUser(user);

        return user;
    }

    public UserResponseDTO getAuthUserDTO(String token) throws ApiException {
        token = extractToken(token);
        User user = getAuthUser();
        Date expirationDate = tokenService.getExpirationDate(token);

        return new UserResponseDTO(token, expirationDate, user);
    }

    @NonNull
    @Override
    public User loadUserByUsername(@NonNull String username) {
        try {
            return userService.findByEmail(username);
        } catch (UsernameNotFoundException | ApiException e) {
            log.debug("User not found for username: {}", username);
            throw new UsernameNotFoundException(ApiMessageEnum.USER_NOT_FOUND.getMessage());
        }
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validatePassword(String rawPassword, String encryptedPassword) throws ApiException {
        boolean isValidPassword = passwordEncoder.matches(rawPassword, encryptedPassword);

        if (!isValidPassword) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ApiMessageEnum.INVALID_PASSWORD);
        }
    }

    private ApiException unauthorized() {
        log.warn("Login failed: invalid credentials");

        return new ApiException(HttpStatus.UNAUTHORIZED, ApiMessageEnum.INVALID_USER_OR_PASSWORD);
    }

    private void validateActiveUser(User user) throws ApiException {
        if (!user.isEnabled()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ApiMessageEnum.USER_DISABLED);
        }
    }

    private String extractToken(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }
}
