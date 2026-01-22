package az.ingress.mapper;

import az.ingress.dao.entity.CategoryEntity;
import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static az.ingress.model.enums.CategoryStatus.ACTIVE;

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

    public List<CategoryResponse> toResponseList(List<CategoryEntity> categories) {
        Map<Long, CategoryResponse> idToNode = categories.stream()
                .map(cat -> new CategoryResponse(
                        cat.getId(),
                        cat.getName(),
                        cat.getSlug(),
                        cat.getStatus(),
                        cat.getSortOrder(),
                        new ArrayList<>()
                ))
                .collect(Collectors.toMap(CategoryResponse::getId, Function.identity()));

        List<CategoryResponse> roots = new ArrayList<>();
        for (CategoryEntity cat : categories) {
            CategoryResponse node = idToNode.get(cat.getId());
            if (cat.getParent() != null) {
                CategoryResponse parentNode = idToNode.get(cat.getParent().getId());
                parentNode.getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        sortChildrenRecursively(roots);

        return roots;
    }

    private void sortChildrenRecursively(List<CategoryResponse> nodes) {
        nodes.sort(Comparator.comparing(CategoryResponse::getSortOrder));
        nodes.forEach(node -> sortChildrenRecursively(node.getChildren()));
    }

}
