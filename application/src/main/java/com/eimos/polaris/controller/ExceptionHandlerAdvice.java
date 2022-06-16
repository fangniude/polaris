package com.eimos.polaris.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.jooq.exception.DataAccessException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * @author lipengpeng
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler({DataAccessException.class, PSQLException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            final RuntimeException exception,
            final WebRequest request) {
        final String msg = this.rootCause(exception).getMessage();
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        msg.isBlank() ? exception.getMessage() : msg,
                        LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd hh:mm:ss")
                ));
    }

    private Throwable rootCause(final Throwable exception) {
        return exception.getCause() != null ? this.rootCause(exception.getCause()) : exception;
    }

    public record ErrorResponse(int status,
                                String error,
                                String path,
                                String message,
                                String timestamp) {
    }
}
