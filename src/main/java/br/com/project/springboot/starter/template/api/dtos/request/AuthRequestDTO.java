package br.com.project.springboot.starter.template.api.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    @Email(message = "{message.api.field.validator.invalid_email}")
    @NotNull(message = "{message.api.field.validator.required_email}")
    private String email;
    @NotNull(message = "{message.api.field.validator.required_password}")
    private String password;
}
