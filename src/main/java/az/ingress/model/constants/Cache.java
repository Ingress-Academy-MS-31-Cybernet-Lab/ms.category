package az.ingress.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Cache {
    public static final String CATEGORY_CACHE_KEY = "ms-category::categories";
    public static final int CACHE_EXPIRATION_HOURS = 24;
}
