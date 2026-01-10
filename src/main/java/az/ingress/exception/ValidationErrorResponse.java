package az.ingress.exception;

import java.util.List;

public record ValidationErrorResponse(List<String> errors) {}