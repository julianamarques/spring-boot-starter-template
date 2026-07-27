package br.com.project.springboot.starter.template.api.utils;

import br.com.project.springboot.starter.template.api.enums.LogContextEnum;
import br.com.project.springboot.starter.template.api.exceptions.ApiExternalException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.UUID;

@Log4j2
public class LogUtils {
    private static final String REQUEST_UUID = "requestUuid";
    private static final String REQUEST_PATH_CONTEXT = "requestPathContext";

    public static void logRequestUuid(String requestUuid) {
        try {
            String requestId = Objects.nonNull(requestUuid) ? requestUuid : UUID.randomUUID().toString();
            MDC.put(LogUtils.REQUEST_UUID, String.format("[%s]", requestId));
        } catch (Exception e) {
            log.warn("Erro ao adicionar no log o UUID da requisição", e);
        }
    }

    public static void logRequestPathContext(LogContextEnum context, String arg) {
        try {
            MDC.put(LogUtils.REQUEST_PATH_CONTEXT, String.format("[%s]", context.getDescription(arg)));
        } catch (Exception e) {
            log.warn("Erro ao adicionar no log o request path da requisição", e);
        }
    }

    public static void clearRequestPathContext(LogContextEnum logContext) {
        try {
            if (requestPathContextExists(logContext)) {
                MDC.remove(LogUtils.REQUEST_PATH_CONTEXT);
            }
        } catch (Exception e) {
            log.warn(" Erro ao limpar o requestPathContext", e);
        }
    }

    public static void clearRequestUuid() {
        try {
            MDC.remove(LogUtils.REQUEST_UUID);
        } catch (Exception e) {
            log.warn("Erro ao limpar o requestUuid", e);
        }
    }

    public static boolean requestPathContextExists(LogContextEnum logContext) {
        try {
            String log = MDC.get(LogUtils.REQUEST_PATH_CONTEXT);

            if (Objects.nonNull(log)) {
                return log.matches(LogUtils.buildRequestPathContextRegex(logContext));
            }
        } catch (Exception e) {
            log.warn(" Erro ao recuperar o request context path", e);
        }

        return false;
    }

    private static String buildRequestPathContextRegex(LogContextEnum logContext) {
        return "\\[" + logContext.getDescription() + ".*\\]";
    }

    public static void logExternalWarn(ApiExternalException exception) {
        log.warn("Um erro de client externo com o status {}: [ {} ] chamada: [ {} ]", exception.getHttpStatus().value(),
                exception.getReason(), exception.getMethodKey());
        log.warn("Response: [ {} ]", exception.getResponseBody());
    }

    public static void logExternalWarn(Integer status, String exception) {
        log.error("Um erro externo de client com status {} ocorreu", status);
        log.error("Response: [ {} ]", exception);
    }
}
