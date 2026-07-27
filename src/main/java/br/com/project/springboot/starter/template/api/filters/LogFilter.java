package br.com.project.springboot.starter.template.api.filters;

import br.com.project.springboot.starter.template.api.enums.LogContextEnum;
import br.com.project.springboot.starter.template.api.utils.LogUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Log4j2
@RequiredArgsConstructor
public class LogFilter extends OncePerRequestFilter {
    private final static String REQUEST_TIME = "requestTime";
    private final static String REQUEST_UUID = "requestUuid";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        beforeRequest(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            afterRequest(request);
        }
    }

    private void beforeRequest(HttpServletRequest request) {
        initLog(request);
        logUrlQueryParam(request);
    }

    private void afterRequest(HttpServletRequest request) {
        try {
            long requestTime = getRequestTime(request);
            log.info("Request completed in {} ms.", requestTime);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        LogUtils.clearRequestPathContext(LogContextEnum.API_CONTEXT);
        LogUtils.clearRequestUuid();
    }

    private long getRequestTime(HttpServletRequest request) {
        return System.currentTimeMillis() - (long) request.getAttribute(REQUEST_TIME);
    }

    private void initLog(HttpServletRequest request) {
        request.setAttribute(REQUEST_TIME, System.currentTimeMillis());
        String requestInfo = String.format("( %s ) %s", request.getMethod(), request.getRequestURI());
        LogUtils.logRequestUuid(request.getHeader(REQUEST_UUID));
        LogUtils.logRequestPathContext(LogContextEnum.API_CONTEXT, requestInfo);

        log.info("Init request");
    }

    private void logUrlQueryParam(HttpServletRequest request) {
        if (Objects.nonNull(request.getQueryString())) {
            String[] paramQueries = request.getQueryString().split("&");

            log.info("======== URL Parameters ==========");

            for (String query : paramQueries) {
                log.info(query);
            }

            log.info("=========================");
        }

        log.info("");
    }
}
