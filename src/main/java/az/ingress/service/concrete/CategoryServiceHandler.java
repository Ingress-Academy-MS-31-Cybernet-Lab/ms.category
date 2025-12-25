package az.ingress.service.concrete;

import az.ingress.dao.entity.CategoryEntity;
import az.ingress.dao.enums.CategoryLevel;
import az.ingress.dao.repository.CategoryRepository;
import az.ingress.exception.ConflictException;
import az.ingress.exception.NotFoundException;
import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;
import az.ingress.model.response.CategorySimpleResponse;
import az.ingress.model.response.CategoryTreeResponse;
import az.ingress.service.abstraction.CategoryService;
import az.ingress.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static az.ingress.exception.ErrorMessage.CATEGORY_NOT_FOUND;
import static az.ingress.exception.ErrorMessage.CATEGORY_SLUG_CONFLICT;
import static az.ingress.mapper.CategoryMapper.CATEGORY_MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceHandler implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CacheUtil cacheUtil;

    private static final String ROOT_CATEGORIES_CACHE_KEY = "ms-category::categories:root";
    private static final String CATEGORY_TREE_CACHE_KEY = "ms-category::categories:tree";
    private static final String CHILDREN_BY_PARENT_CACHE_KEY = "ms-category::categories:children:";
    private static final Long CACHE_EXPIRE_TIME = 24L;

    @Override
    public void createCategory(CategoryRequest categoryRequest) {
        log.info("ActionLog.createCategory.start - {}", categoryRequest);

        var category = CATEGORY_MAPPER.buildCategoryEntity(categoryRequest);

        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new ConflictException(CATEGORY_SLUG_CONFLICT, category.getSlug());
        }

        if (categoryRequest.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND, categoryRequest.getParentId()));

            category.setParent(parent);
            category.setLevel(
                    parent.getLevel() == CategoryLevel.ROOT ? CategoryLevel.PARENT : CategoryLevel.CHILD
            );
        } else {
            category.setLevel(CategoryLevel.ROOT);
        }

        categoryRepository.save(category);

        clearAllCaches();

        log.info("ActionLog.createCategory.end - saved category and cleared cache");
    }

    @Override
    public List<CategorySimpleResponse> getRootCategories() {
        log.info("ActionLog.getRootCategories.start");

        List<CategorySimpleResponse> cachedRootCategories = cacheUtil.getBucket(ROOT_CATEGORIES_CACHE_KEY);

        if (cachedRootCategories != null) {
            log.info("ActionLog.getRootCategories.end - returned from cache");
            return cachedRootCategories;
        }

        List<CategoryEntity> rootCategories = categoryRepository.findByParentIsNullOrderBySortOrderAsc();
        List<CategorySimpleResponse> response = CATEGORY_MAPPER.toSimpleResponseList(rootCategories);

        cacheUtil.saveToCache(ROOT_CATEGORIES_CACHE_KEY, response, CACHE_EXPIRE_TIME, ChronoUnit.HOURS);

        log.info("ActionLog.getRootCategories.end - returned from database and cached");
        return response;
    }

    @Override
    public List<CategoryResponse> getChildrenByParent(Long parentId) {
        log.info("ActionLog.getChildrenByParent.start - parentId: {}", parentId);

        String cacheKey = CHILDREN_BY_PARENT_CACHE_KEY + parentId;
        List<CategoryResponse> cachedChildren = cacheUtil.getBucket(cacheKey);

        if (cachedChildren != null) {
            log.info("ActionLog.getChildrenByParent.end - returned from cache");
            return cachedChildren;
        }

        List<CategoryEntity> children = categoryRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(parentId);
        List<CategoryResponse> response = CATEGORY_MAPPER.toCategoryResponseList(children);

        cacheUtil.saveToCache(cacheKey, response, CACHE_EXPIRE_TIME, ChronoUnit.HOURS);

        log.info("ActionLog.getChildrenByParent.end - returned from database and cached");
        return response;
    }

    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        log.info("ActionLog.getCategoryTree.start");

        List<CategoryTreeResponse> cachedTree = cacheUtil.getBucket(CATEGORY_TREE_CACHE_KEY);

        if (cachedTree != null) {
            log.info("ActionLog.getCategoryTree.end - returned from cache");
            return cachedTree;
        }

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryTreeResponse> response = CATEGORY_MAPPER.toTreeResponseList(categories);

        cacheUtil.saveToCache(CATEGORY_TREE_CACHE_KEY, response, CACHE_EXPIRE_TIME, ChronoUnit.HOURS);

        log.info("ActionLog.getCategoryTree.end - returned from database and cached");
        return response;
    }

    private void clearAllCaches() {
        log.info("ActionLog.clearAllCaches.start");

        cacheUtil.deleteKey(ROOT_CATEGORIES_CACHE_KEY);
        cacheUtil.deleteKey(CATEGORY_TREE_CACHE_KEY);
        cacheUtil.deleteKeysByPattern(CHILDREN_BY_PARENT_CACHE_KEY + "*");

        log.info("ActionLog.clearAllCaches.end");
    }
}
