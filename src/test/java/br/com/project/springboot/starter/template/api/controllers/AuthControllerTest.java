package br.com.project.springboot.starter.template.api.controllers;

import br.com.project.springboot.starter.template.api.TestcontainersConfiguration;
import br.com.project.springboot.starter.template.api.entities.User;
import br.com.project.springboot.starter.template.api.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AuthControllerTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_MAP =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Value("${application.key.jwt.secret}")
    private String jwtSecret;

    @Test
    void createUser_returnsEnvelopedAccessToken() {
        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", newUser("create@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("status").containsKey("message").containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(201);
        assertThat(data(response).get("accessToken")).asString().isNotBlank();
        assertThat(data(response).get("email")).isEqualTo("create@example.com");
    }

    @Test
    void login_withValidCredentials_returnsEnvelopedAccessToken() {
        createUser("login@example.com");

        ResponseEntity<Map<String, Object>> response = post("/auth/login",
                Map.of("email", "login@example.com", "password", "Password123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(data(response).get("accessToken")).asString().isNotBlank();
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() {
        createUser("wrongpass@example.com");

        ResponseEntity<Map<String, Object>> response = post("/auth/login",
                Map.of("email", "wrongpass@example.com", "password", "WrongPassword"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("message")).asString().isNotBlank();
    }

    @Test
    void login_withUnknownUser_returnsUnauthorized() {
        ResponseEntity<Map<String, Object>> response = post("/auth/login",
                Map.of("email", "ghost@example.com", "password", "Password123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createUser_withDuplicateEmail_returnsBadRequestWithMessageAndNoSqlLeak() {
        createUser("dup@example.com");

        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", newUser("dup@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Email existe");
        assertThat(response.getBody().toString())
                .doesNotContainIgnoringCase("constraint")
                .doesNotContainIgnoringCase("uk_email");
    }

    @Test
    void createUser_withInvalidEmail_returnsBadRequest() {
        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", Map.of(
                "name", "Bad",
                "email", "not-an-email",
                "password", "Password123",
                "confirmPassword", "Password123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(data(response)).containsKey("email");
    }

    @Test
    void protectedRoute_withoutToken_returnsCleanUnauthorizedEnvelope() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri("/auth/user"), HttpMethod.GET,
                HttpEntity.EMPTY, JSON_MAP);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("message")).isEqualTo("Acesso negado");
        assertThat(response.getBody()).doesNotContainKeys("headers", "statusCode", "statusCodeValue");
    }

    @Test
    void protectedRoute_withValidToken_returnsOk() {
        String token = createUser("protected@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri("/auth/user"), HttpMethod.GET,
                new HttpEntity<>(headers), JSON_MAP);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(data(response).get("email")).isEqualTo("protected@example.com");
    }

    @Test
    void protectedRoute_withMalformedToken_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt");
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri("/auth/user"), HttpMethod.GET,
                new HttpEntity<>(headers), JSON_MAP);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("message")).isEqualTo("Acesso negado");
    }

    @Test
    void protectedRoute_withExpiredToken_returnsUnauthorized() {
        String token = expiredToken("expired@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri("/auth/user"), HttpMethod.GET,
                new HttpEntity<>(headers), JSON_MAP);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("message")).isEqualTo("Acesso negado");
    }

    @Test
    void login_withDisabledUser_returnsUnauthorized() {
        createUser("disabled@example.com");
        User user = userRepository.findByEmail("disabled@example.com").orElseThrow();
        user.setActive(false);
        userRepository.save(user);

        ResponseEntity<Map<String, Object>> response = post("/auth/login",
                Map.of("email", "disabled@example.com", "password", "Password123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void editUser_withValidToken_updatesNameAndReturnsOk() {
        String token = createUser("editme@example.com");

        Map<String, String> body = Map.of(
                "name", "Updated Name",
                "email", "editme@example.com",
                "password", "NewPassword123",
                "confirmPassword", "NewPassword123");
        ResponseEntity<Map<String, Object>> response = putWithAuth("/auth/edit-user", token, body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(data(response).get("name")).isEqualTo("Updated Name");
    }

    @Test
    void editUser_withEmailBelongingToAnotherUser_returnsBadRequest() {
        createUser("edit-dup-existing@example.com");
        String token = createUser("edit-dup-self@example.com");

        ResponseEntity<Map<String, Object>> response = putWithAuth("/auth/edit-user", token,
                newUser("edit-dup-existing@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Email existe");
    }

    private String createUser(String email) {
        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", newUser(email));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return (String) data(response).get("accessToken");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> data(ResponseEntity<Map<String, Object>> response) {
        return (Map<String, Object>) response.getBody().get("body");
    }

    private ResponseEntity<Map<String, Object>> post(String path, Object body) {
        return restTemplate.exchange(uri(path), HttpMethod.POST, json(body), JSON_MAP);
    }

    private ResponseEntity<Map<String, Object>> putWithAuth(String path, String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(uri(path), HttpMethod.PUT, new HttpEntity<>(body, headers), JSON_MAP);
    }

    private HttpEntity<Object> json(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String expiredToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    private String uri(String path) {
        return "http://localhost:" + port + "/base-url" + path;
    }

    private Map<String, String> newUser(String email) {
        return Map.of(
                "name", "Test User",
                "email", email,
                "password", "Password123",
                "confirmPassword", "Password123");
    }
}
