package br.com.project.springboot.starter.template.api.enums;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

@Getter
public enum ApiMessageEnum {
    REQUEST_COMPLETED("message.api.request_completed"),
    ROUTE_NOT_FOUND("message.api.route_not_found"),
    REQUEST_PARAMETERS_MISSING("message.api.request_parameters_missing"),
    ENDPOINT_ERROR("message.api.endpoint_error"),
    INVALID_ARGUMENT("message.api.invalid_argument"),
    UNSUPPORTED_METHOD("message.api.unsupported_method"),
    ACCESS_DENIED("message.api.access_denied"),
    UNKNOWN_ERROR("message.api.unknown_error"),
    SSL_HANDSHAKE_ERROR("message.api.ssl_handshake_error"),
    EXTERNAL_SERVICE_UNAVAILABLE("message.api.external.unavailable_service"),
    USER_NOT_FOUND("message.api.user_not_found"),
    EMAIL_EXISTS("message.api.email_exists"),
    INVALID_PASSWORD("message.api.invalid_password"),
    INVALID_USER_OR_PASSWORD("message.api.user_or_password_invalid"),
    USER_DISABLED("message.api.user_disabled"),
    DATA_INTEGRITY_VIOLATION("message.api.data_integrity_violation"),
    ROLE_NOT_FOUND("message.api.role_not_found");

    ApiMessageEnum(String description) {
        this.description = description;
    }

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
    private final String description;

    public String getMessage(String... args) {
        String message = resourceBundle.getString(this.description);

        if (Objects.isNull(args) || args.length == 0) {
            return message;
        }

        return MessageFormat.format(message, (Object[]) args);
    }
}
