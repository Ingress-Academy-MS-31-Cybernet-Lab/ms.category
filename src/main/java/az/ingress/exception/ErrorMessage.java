package az.ingress.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    UNEXPECTED_ERROR("error.category.unexpected"),
    CATEGORY_SLUG_ALREADY_EXISTS("error.category.slug.already.exists"),
    CATEGORY_NOT_FOUND("error.category.not.found");

    private final String value;
}