package az.ingress.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    UNEXPECTED_ERROR("unexpected.error"),
    CATEGORY_SLUG_CONFLICT("category.slug"),
    CATEGORY_NOT_FOUND("error.category.not.found");

    private final String value;
}