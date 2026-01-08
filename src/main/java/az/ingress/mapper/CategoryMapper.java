package az.ingress.mapper;

import az.ingress.dao.entity.CategoryEntity;
import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;
import az.ingress.model.response.CategorySimpleResponse;
import az.ingress.model.response.CategoryTreeResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static az.ingress.model.enums.Status.ACTIVE;

public enum CategoryMapper {
    CATEGORY_MAPPER;

    public CategoryEntity buildCategoryEntity(CategoryRequest categoryRequest) {
        return CategoryEntity.builder()
                .name(categoryRequest.getName())
                .slug(categoryRequest.getSlug())
                .sortOrder(
                        categoryRequest.getSortOrder() != null
                                ? categoryRequest.getSortOrder()
                                : 0
                )
                .status(ACTIVE)
                .build();
    }

    public CategoryResponse toCategoryResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .status(entity.getStatus())
                .sortOrder(entity.getSortOrder())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }

    public List<CategoryResponse> toCategoryResponseList(List<CategoryEntity> categories) {
        return categories.stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    public CategorySimpleResponse toSimpleResponse(CategoryEntity entity) {
        return CategorySimpleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<CategorySimpleResponse> toSimpleResponseList(List<CategoryEntity> categories) {
        return categories.stream()
                .map(this::toSimpleResponse)
                .toList();
    }

    public List<CategoryTreeResponse> toTreeResponseList(List<CategoryEntity> categories) {
        Map<Long, CategoryTreeResponse> idToNode = categories.stream()
                .map(cat -> new CategoryTreeResponse(
                        cat.getId(),
                        cat.getName(),
                        cat.getSlug(),
                        cat.getSortOrder(),
                        cat.getStatus(),
                        new ArrayList<>()
                ))
                .collect(Collectors.toMap(CategoryTreeResponse::getId, Function.identity()));

        List<CategoryTreeResponse> roots = new ArrayList<>();
        for (CategoryEntity cat : categories) {
            CategoryTreeResponse node = idToNode.get(cat.getId());
            if (cat.getParent() != null) {
                CategoryTreeResponse parentNode = idToNode.get(cat.getParent().getId());
                parentNode.getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        sortChildrenRecursively(roots);

        return roots;
    }

    private void sortChildrenRecursively(List<CategoryTreeResponse> nodes) {
        nodes.sort(Comparator.comparing(CategoryTreeResponse::getSortOrder));
        nodes.forEach(node -> sortChildrenRecursively(node.getChildren()));
    }

}
