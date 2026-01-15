package az.ingress.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Builder
@JsonInclude(NON_EMPTY)
public record ErrorResponse(String message, List<String> errors) {
}