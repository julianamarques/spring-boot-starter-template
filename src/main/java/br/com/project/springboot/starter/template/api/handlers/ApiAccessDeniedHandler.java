package br.com.project.springboot.starter.template.api.handlers;

import br.com.project.springboot.starter.template.api.dtos.response.Response;
import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import br.com.project.springboot.starter.template.api.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    private final JsonUtils jsonUtils;

    @Override
    public void handle(@NonNull HttpServletRequest request, HttpServletResponse servletResponse,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException {
        Response<Object> response = new Response<>(HttpStatus.FORBIDDEN, ApiMessageEnum.ACCESS_DENIED.getMessage(), null);
        servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        servletResponse.setContentType("application/json");
        servletResponse.getWriter().print(jsonUtils.objectToJson(response));
    }
}
