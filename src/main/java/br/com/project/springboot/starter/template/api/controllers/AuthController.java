package br.com.project.springboot.starter.template.api.controllers;

import br.com.project.springboot.starter.template.api.dtos.request.AuthRequestDTO;
import br.com.project.springboot.starter.template.api.dtos.request.UserRequestDTO;
import br.com.project.springboot.starter.template.api.dtos.response.Response;
import br.com.project.springboot.starter.template.api.dtos.response.UserResponseDTO;
import br.com.project.springboot.starter.template.api.exceptions.ApiException;
import br.com.project.springboot.starter.template.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<UserResponseDTO>> auth(@Valid @RequestBody AuthRequestDTO request) throws ApiException {
        UserResponseDTO body = authService.auth(request);

        return Response.success(body);
    }

    @GetMapping("/user")
    public ResponseEntity<Response<UserResponseDTO>> getAuthUser(@RequestHeader("Authorization") String token) throws ApiException {
        UserResponseDTO body = authService.getAuthUserDTO(token);

        return Response.success(body);
    }

    @PostMapping("/create-user")
    public ResponseEntity<Response<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO request) throws ApiException {
        UserResponseDTO body = authService.save(request);

        return Response.created(body);
    }

    @PutMapping("/edit-user")
    public ResponseEntity<Response<UserResponseDTO>> editUser(@RequestHeader("Authorization") String token,
                                                             @Valid @RequestBody UserRequestDTO request) throws ApiException {
        UserResponseDTO body = authService.edit(token, request);

        return Response.success(body);
    }
}
