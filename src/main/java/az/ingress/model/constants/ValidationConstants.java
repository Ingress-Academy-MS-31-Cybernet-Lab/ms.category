package az.ingress.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ValidationConstants {
    public static final String CATEGORY_NAME_NOT_BLANK = "validation.category.name.not.blank";
    public static final String CATEGORY_SLUG_NOT_BLANK = "validation.category.slug.not.blank";
}
