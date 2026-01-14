package az.ingress.exception;

import az.ingress.logger.ApplicationLogger;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static az.ingress.exception.ErrorMessage.CATEGORY_METHOD_NOT_ALLOWED;
import static az.ingress.exception.ErrorMessage.CATEGORY_NOT_FOUND;
import static az.ingress.exception.ErrorMessage.CATEGORY_SLUG_ALREADY_EXISTS;
import static az.ingress.exception.ErrorMessage.UNEXPECTED_ERROR;
import static az.ingress.exception.ErrorMessage.VALIDATION_ERROR;
import static az.ingress.model.constants.LocalizationConstants.ERROR_BUNDLE;
import static az.ingress.model.constants.LocalizationConstants.VALIDATION_BUNDLE;
import static az.ingress.util.LocalizationUtil.LOCALIZATION_UTIL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ErrorHandler {
    private final ApplicationLogger log = ApplicationLogger.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception ex) {
        log.error("Exception: ", ex);
        String message = LOCALIZATION_UTIL.getMessageByKey(ERROR_BUNDLE, UNEXPECTED_ERROR.getValue());

        return new ErrorResponse(message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: ", ex);
        String message = LOCALIZATION_UTIL.getMessageByKey(ERROR_BUNDLE, CATEGORY_METHOD_NOT_ALLOWED.getValue());

        return new ErrorResponse(message);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(ConflictException ex) {
        log.error("ConflictException: ", ex);
        var message = LOCALIZATION_UTIL.getMessageByKey(ERROR_BUNDLE, CATEGORY_SLUG_ALREADY_EXISTS.getValue());
        return new ErrorResponse(message);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(NotFoundException ex) {
        log.error("NotFoundException: ", ex);
        var message = LOCALIZATION_UTIL.getMessageByKey(ERROR_BUNDLE, CATEGORY_NOT_FOUND.getValue());
        return new ErrorResponse(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(BindException ex) {
        log.error("BindException: ", ex);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fieldName = error.getField();
                    String errorKey = error.getDefaultMessage();
                    String errorMessage = LOCALIZATION_UTIL.getMessageByKey(VALIDATION_BUNDLE, errorKey);
                    return fieldName + ": " + errorMessage;
                })
                .toList();

        String message = LOCALIZATION_UTIL.getMessageByKey(ERROR_BUNDLE, VALIDATION_ERROR.getValue());

        return new ErrorResponse(message, errors);
    }
}