package com.ahmadabbas.filetracking.backend.exception;

import com.ahmadabbas.filetracking.backend.exception.payload.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                        WebRequest webRequest) {
        log.error("ResourceNotFoundException: ", exception);
        ErrorDetails errorDetails = new ErrorDetails(
                null,
                exception.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(APIException exception,
                                                           WebRequest webRequest) {
        log.error("APIException: ", exception);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(ZoneId.of("Europe/Athens")),
                exception.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException accessDeniedException,
                                                                    WebRequest webRequest) {
        log.error("AccessDeniedException: ", accessDeniedException);
        String logMessage = "[%s] %s".formatted(accessDeniedException.getClass().getSimpleName(),
                accessDeniedException.getMessage());
        log.debug("Handing exception '{}'", logMessage);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(ZoneId.of("Europe/Athens")),
                accessDeniedException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException authenticationException,
                                                                      WebRequest webRequest) {
        log.error("AuthenticationException: ", authenticationException);
        String message = authenticationException.getMessage();
        if (authenticationException instanceof BadCredentialsException) {
            message = "Incorrect email or password";
        } else if (authenticationException instanceof CredentialsExpiredException) {
            message = "User credentials are expired";
        } else if (authenticationException instanceof DisabledException) {
            message = "User is disabled";
        }
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(ZoneId.of("Europe/Athens")),
                message,
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateResourceException(Exception exception,
                                                                         WebRequest webRequest) {
        log.error("DuplicateResourceException: ", exception);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(ZoneId.of("Europe/Athens")),
                exception.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception,
                                                              WebRequest webRequest) {
        log.error("GlobalException: ", exception);
        String message = exception.getMessage();
        if (exception instanceof InvalidDataAccessApiUsageException) {
            message = "Incorrect data";
        }
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(ZoneId.of("Europe/Athens")),
                message,
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.error("MethodArgumentNotValidException: ", ex);
        HashMap<Object, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest webRequest) {
        log.error("HttpMessageNotReadableException: ", exception);
        String message = exception.getMessage();
        if (exception.getMessage().toLowerCase().contains("cannot deserialize")) {
            message = "Problem deserializing fields, make sure fields are sent correctly!";
        } else if (message.contains(", problem: ")) {
            message = message.split(", problem: ")[1];
        }
        ErrorDetails errorDetails = new ErrorDetails(
                null,
                message,
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
