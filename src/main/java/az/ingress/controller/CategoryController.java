package az.ingress.controller;

import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryTreeResponse;
import az.ingress.service.abstraction.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
    }

    @GetMapping
    public List<CategoryTreeResponse> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
}