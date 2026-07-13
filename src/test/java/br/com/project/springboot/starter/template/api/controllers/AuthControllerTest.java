package br.com.project.springboot.starter.template.api.controllers;

import br.com.project.springboot.starter.template.api.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
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

    @Test
    void createUser_returnsEnvelopedAccessToken() {
        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", newUser("create@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("status").containsKey("message").containsKey("timestamp");
        assertThat(response.getBody().get("status")).isEqualTo(200);
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

    private String createUser(String email) {
        ResponseEntity<Map<String, Object>> response = post("/auth/create-user", newUser(email));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return (String) data(response).get("accessToken");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> data(ResponseEntity<Map<String, Object>> response) {
        return (Map<String, Object>) response.getBody().get("body");
    }

    private ResponseEntity<Map<String, Object>> post(String path, Object body) {
        return restTemplate.exchange(uri(path), HttpMethod.POST, json(body), JSON_MAP);
    }

    private HttpEntity<Object> json(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
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
