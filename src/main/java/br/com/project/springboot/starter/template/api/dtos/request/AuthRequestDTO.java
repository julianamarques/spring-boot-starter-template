package br.com.project.springboot.starter.template.api.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    @Email(message = "{message.api.field.validator.invalid_email}")
    @NotBlank(message = "{message.api.field.validator.required_email}")
    private String email;
    @NotBlank(message = "{message.api.field.validator.required_password}")
    private String password;
}
