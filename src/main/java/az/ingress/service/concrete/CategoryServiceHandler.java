package az.ingress.service.concrete;

import az.ingress.dao.entity.CategoryEntity;
import az.ingress.dao.repository.CategoryRepository;
import az.ingress.exception.ConflictException;
import az.ingress.exception.NotFoundException;
import az.ingress.logger.ApplicationLogger;
import az.ingress.model.request.CategoryRequest;
import az.ingress.model.response.CategoryResponse;
import az.ingress.service.abstraction.CategoryService;
import az.ingress.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static az.ingress.exception.ErrorMessage.CATEGORY_NOT_FOUND;
import static az.ingress.exception.ErrorMessage.CATEGORY_SLUG_ALREADY_EXISTS;
import static az.ingress.mapper.CategoryMapper.CATEGORY_MAPPER;
import static az.ingress.model.constants.Cache.CACHE_EXPIRATION_HOURS;
import static az.ingress.model.constants.Cache.CATEGORY_CACHE_KEY;
import static java.time.temporal.ChronoUnit.HOURS;

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
            log.error("ActionLog.createCategory.error.categoryAlreadyExists - {}", categoryRequest.getSlug());
            throw new ConflictException(CATEGORY_SLUG_ALREADY_EXISTS, categoryRequest.getSlug());
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
    public List<CategoryResponse> getCategories() {
        log.info("ActionLog.getCategories.start");
        List<CategoryResponse> response;

        response = cacheUtil.getBucket(CATEGORY_CACHE_KEY);

        if (response != null) {
            log.info("ActionLog.getCategories.end - returned from cache");
            return response;
        }

        List<CategoryEntity> categories = categoryRepository.findAll();
        response = CATEGORY_MAPPER.toResponseList(categories);

        cacheUtil.saveToCache(CATEGORY_CACHE_KEY, response, CACHE_EXPIRATION_HOURS, HOURS);

        log.info("ActionLog.getCategories.end - returned from database and cached");
        return response;
    }

    private void clearAllCaches() {
        log.info("ActionLog.clearAllCaches.start");
        cacheUtil.deleteKey(CATEGORY_CACHE_KEY);
        log.info("ActionLog.clearAllCaches.end");
    }
}
