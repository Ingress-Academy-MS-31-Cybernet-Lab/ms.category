package az.ingress.service.abstraction;

import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequest categoryRequest);

    List<CategoryResponse> getCategories();
}
