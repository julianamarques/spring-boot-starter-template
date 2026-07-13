package br.com.project.springboot.starter.template.api.dtos.request;

import br.com.project.springboot.starter.template.api.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class UserRequestDTO {
    @NotNull(message = "{message.api.field.validator.required_name}")
    private String name;
    @Email(message = "{message.api.field.validator.invalid_email}")
    @NotNull(message = "{message.api.field.validator.required_email}")
    private String email;
    @NotNull(message = "{message.api.field.validator.required_password}")
    private String password;
    @NotNull(message = "{message.api.field.validator.required_password_confirm}")
    private String confirmPassword;

    public User convertToEntity(String encriptedPassword) {
        return convertToEntity(new User(), encriptedPassword);
    }

    public User convertToEntity(User user, String encriptedPassword) {
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(encriptedPassword);

        if (Objects.isNull(user.getActive())) {
            user.setActive(true);
        }

        return user;
    }
}
