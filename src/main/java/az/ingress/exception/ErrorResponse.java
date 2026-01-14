package az.ingress.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
public record ErrorResponse(
        String message,
        List<String> errors
) {
    public ErrorResponse(String message) {
        this(message, null);
    }
}