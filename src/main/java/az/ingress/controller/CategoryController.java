package az.ingress.controller;

import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;
import az.ingress.model.response.CategorySimpleResponse;
import az.ingress.model.response.CategoryTreeResponse;
import az.ingress.service.abstraction.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
    }

    @GetMapping("/root")
    public List<CategorySimpleResponse> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @GetMapping("/tree")
    public List<CategoryTreeResponse> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/parent/{parentId}")
    public List<CategoryResponse> getChildrenByParent(@PathVariable Long parentId) {
        return categoryService.getChildrenByParent(parentId);
    }
}