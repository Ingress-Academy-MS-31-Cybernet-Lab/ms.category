package az.ingress.service.concrete;

import az.ingress.dao.entity.CategoryEntity;
import az.ingress.dao.repository.CategoryRepository;
import az.ingress.exception.ConflictException;
import az.ingress.exception.NotFoundException;
import az.ingress.logger.ApplicationLogger;
import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryTreeResponse;
import az.ingress.service.abstraction.CategoryService;
import az.ingress.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static az.ingress.exception.ErrorMessage.CATEGORY_NOT_FOUND;
import static az.ingress.exception.ErrorMessage.CATEGORY_SLUG_CONFLICT;
import static az.ingress.mapper.CategoryMapper.CATEGORY_MAPPER;
import static az.ingress.model.constants.Cache.CACHE_EXPIRE_TIME;
import static az.ingress.model.constants.Cache.CATEGORY_TREE_CACHE_KEY;

@Service
@RequiredArgsConstructor
public class CategoryServiceHandler implements CategoryService {
    private final ApplicationLogger log = ApplicationLogger.getLogger(CategoryServiceHandler.class);

    private final CategoryRepository categoryRepository;
    private final CacheUtil cacheUtil;

    @Override
    @Transactional
    public void createCategory(CategoryRequest categoryRequest) {
        log.info("ActionLog.createCategory.start - {}", categoryRequest);

        if (categoryRepository.existsBySlug(categoryRequest.getSlug())) {
            throw new ConflictException(CATEGORY_SLUG_CONFLICT, categoryRequest.getSlug());
        }

        var category = CATEGORY_MAPPER.buildCategoryEntity(categoryRequest);

        if (categoryRequest.getParentId() != null) {
            var parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND, categoryRequest.getParentId()));
            category.setParent(parent);
        }

        categoryRepository.save(category);
        clearAllCaches();

        log.info("ActionLog.createCategory.end - saved category and cleared cache");
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
        cacheUtil.deleteKey(CATEGORY_TREE_CACHE_KEY);
        log.info("ActionLog.clearAllCaches.end");
    }
}
