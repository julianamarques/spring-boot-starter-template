package br.com.project.springboot.starter.template.api.configs.clients;

import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import br.com.project.springboot.starter.template.api.exceptions.ApiExternalException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@RequiredArgsConstructor
public class ClientErrorDecoderConfig implements ErrorDecoder {
    private static final int BAD_REQUEST_STATUS = 400;
    private static final int UNPROCESSABLE_ENTITY_STATUS = 422;
    private static final int INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int UNAVAILABLE_SERVICE_STATUS = 503;

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String s, Response response) {
        String url = response.request().url();
        String methodName = response.request().httpMethod().name();

        try (InputStream body = response.body().asInputStream()) {
            Object exceptionMessage = objectMapper.readValue(body, Object.class);

            return switch (response.status()) {
                case BAD_REQUEST_STATUS, UNPROCESSABLE_ENTITY_STATUS -> new ApiExternalException(url, HttpStatus.BAD_REQUEST,
                        exceptionMessage, response.reason(), methodName);
                case INTERNAL_SERVER_ERROR_STATUS, UNAVAILABLE_SERVICE_STATUS -> new ApiExternalException(url,
                        resolveStatus(response.status()), ApiMessageEnum.EXTERNAL_SERVICE_UNAVAILABLE.getMessage());
                default -> new ApiExternalException(url, resolveStatus(response.status()), exceptionMessage, response.reason(), methodName);
            };
        } catch (Exception e) {
            return new ApiExternalException(url, resolveStatus(response.status()), response.reason(), methodName);
        }
    }

    private HttpStatus resolveStatus(int statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode);

        return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
