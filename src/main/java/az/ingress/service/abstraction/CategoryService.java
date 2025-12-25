package az.ingress.service.abstraction;

import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;
import az.ingress.model.response.CategorySimpleResponse;
import az.ingress.model.response.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequest categoryRequest);

    List<CategoryTreeResponse> getCategoryTree();

    List<CategorySimpleResponse> getRootCategories();

    List<CategoryResponse> getChildrenByParent(Long parentId);
}
