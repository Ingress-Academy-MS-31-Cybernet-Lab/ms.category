package az.ingress.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ValidationMessages {
    public static final String CATEGORY_NAME_NOT_BLANK = "Category name cannot be blank";
    public static final String CATEGORY_SLUG_NOT_BLANK = "Slug cannot be blank";
}
