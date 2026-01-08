package az.ingress.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Cache {
    public static final String CATEGORY_TREE_CACHE_KEY = "ms-category::categories:tree";
    public static final Long CACHE_EXPIRE_TIME = 24L;
}
