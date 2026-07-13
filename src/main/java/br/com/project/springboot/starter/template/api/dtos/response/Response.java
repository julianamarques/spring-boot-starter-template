package br.com.project.springboot.starter.template.api.dtos.response;

import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class Response<B> implements Serializable {
    private Integer status;
    private String message;
    private LocalDateTime timestamp;
    private B body;

    public Response(HttpStatus status, String message, B body) {
        this.status = status.value();
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.body = body;
    }

    public static <T> ResponseEntity<Response<T>> success(T body) {
        return ResponseEntity.ok(new Response<>(HttpStatus.OK, ApiMessageEnum.REQUEST_COMPLETED.getMessage(), body));
    }

    public static ResponseEntity<Object> error(HttpStatus status, String message, Object body) {
        return new ResponseEntity<>(new Response<>(status, message, body), status);
    }
}
