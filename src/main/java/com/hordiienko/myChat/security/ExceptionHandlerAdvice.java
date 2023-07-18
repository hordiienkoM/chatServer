package com.hordiienko.myChat.security;

import com.hordiienko.myChat.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler
    public ResponseEntity<?> handleException(Throwable throwable) {
        throwable.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(throwable.getMessage());
    }

    @MessageExceptionHandler({Throwable.class})
    public Mono<?> handleMessageException(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.error(throwable);
    }

    @MessageExceptionHandler({MethodArgumentNotValidException.class})
    public Mono<?> handleValidationException(MethodArgumentNotValidException exception) {
        exception.printStackTrace();
        if (exception.getBindingResult() != null) {
            List<ObjectError> errors = exception.getBindingResult().getAllErrors();
            for (ObjectError error: errors) {
                if (error instanceof FieldError fieldError) {
                    return Mono.error(new ValidationException(fieldError.getDefaultMessage()));
                }
            }
        }
        return Mono.error(exception);
    }
}
