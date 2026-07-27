package br.com.project.springboot.starter.template.api.dtos.request;

import br.com.project.springboot.starter.template.api.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class UserRequestDTO {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 72;

    @NotBlank(message = "{message.api.field.validator.required_name}")
    private String name;
    @Email(message = "{message.api.field.validator.invalid_email}")
    @NotBlank(message = "{message.api.field.validator.required_email}")
    private String email;
    @NotBlank(message = "{message.api.field.validator.required_password}")
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = "{message.api.field.validator.password_invalid_size}")
    private String password;
    @NotBlank(message = "{message.api.field.validator.required_password_confirm}")
    private String confirmPassword;

    public User convertToEntity(String encryptedPassword) {
        return convertToEntity(new User(), encryptedPassword);
    }

    public User convertToEntity(User user, String encryptedPassword) {
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(encryptedPassword);

        if (Objects.isNull(user.getActive())) {
            user.setActive(true);
        }

        return user;
    }
}
