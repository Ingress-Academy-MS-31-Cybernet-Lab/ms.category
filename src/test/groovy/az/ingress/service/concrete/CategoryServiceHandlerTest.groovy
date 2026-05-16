package az.ingress.service.concrete

import az.ingress.dao.entity.CategoryEntity
import az.ingress.dao.repository.CategoryRepository
import az.ingress.exception.ConflictException
import az.ingress.exception.NotFoundException
import az.ingress.model.request.CategoryRequest
import az.ingress.model.response.CategoryResponse
import az.ingress.service.abstraction.CategoryService
import az.ingress.util.CacheUtil
import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom
import spock.lang.Specification

import static az.ingress.model.constants.Cache.CACHE_EXPIRATION_HOURS
import static az.ingress.model.constants.Cache.CATEGORY_CACHE_KEY
import static java.time.temporal.ChronoUnit.HOURS

class CategoryServiceHandlerTest extends Specification {
    EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom()
    CategoryService categoryService
    CategoryRepository categoryRepository
    CacheUtil cacheUtil

    def setup() {
        categoryRepository = Mock()
        cacheUtil = Mock()
        categoryService = new CategoryServiceHandler(categoryRepository, cacheUtil)
    }

    def "TestCreateCategory should successfully create category without parent"() {
        given:
        def categoryRequest = random.nextObject(CategoryRequest)
        categoryRequest.parentId = null

        def savedEntity = new CategoryEntity(
                name: categoryRequest.name,
                slug: categoryRequest.slug,
                parent: null
        )

        categoryRepository.existsBySlug(categoryRequest.slug) >> false
        categoryRepository.save(_ as CategoryEntity) >> savedEntity

        when:
        categoryService.createCategory(categoryRequest)

        then:
        1 * categoryRepository.existsBySlug(categoryRequest.slug)
        1 * categoryRepository.save({
            it.slug == categoryRequest.slug &&
                    it.name == categoryRequest.name &&
                    it.parent == null
        })
        1 * cacheUtil.deleteKey(CATEGORY_CACHE_KEY)
        0 * categoryRepository.findById(_)
    }

    def "TestCreateCategory should successfully create category with parent"() {
        given:
        def parentCategory = random.nextObject(CategoryEntity)
        parentCategory.parent = null

        def categoryRequest = random.nextObject(CategoryRequest)
        categoryRequest.parentId = parentCategory.id

        when:
        categoryService.createCategory(categoryRequest)

        then:
        1 * categoryRepository.existsBySlug(categoryRequest.slug) >> false
        1 * categoryRepository.findById(categoryRequest.parentId) >> Optional.of(parentCategory)
        1 * categoryRepository.save(_ as CategoryEntity) >> { CategoryEntity entity ->
            assert entity.slug == categoryRequest.slug
            assert entity.name == categoryRequest.name
            assert entity.parent == parentCategory
            return entity
        }
        1 * cacheUtil.deleteKey(CATEGORY_CACHE_KEY)
    }

    def "TestCreateCategory should throw ConflictException when slug already exists"() {
        given:
        def categoryRequest = random.nextObject(CategoryRequest)

        when:
        categoryService.createCategory(categoryRequest)

        then:
        1 * categoryRepository.existsBySlug(categoryRequest.slug) >> true
        0 * categoryRepository.findById(_)
        0 * categoryRepository.save(_)
        0 * cacheUtil.deleteKey(_)

        and:
        thrown(ConflictException)
    }

    def "TestCreateCategory should throw NotFoundException when parent not found"() {
        given:
        def categoryRequest = random.nextObject(CategoryRequest)

        when:
        categoryService.createCategory(categoryRequest)

        then:
        1 * categoryRepository.existsBySlug(categoryRequest.slug) >> false
        1 * categoryRepository.findById(categoryRequest.parentId) >> Optional.empty()
        0 * categoryRepository.save(_)
        0 * cacheUtil.deleteKey(_)
        and:
        thrown(NotFoundException)
    }

    def "TestGetCategories should return cached data when cache is available"() {
        given:
        def cachedResponse = [random.nextObject(CategoryResponse)]

        when:
        def actual = categoryService.getCategories()

        then:
        1 * cacheUtil.getBucket(CATEGORY_CACHE_KEY) >> cachedResponse
        0 * categoryRepository.findAll()
        0 * cacheUtil.saveToCache(_, _, _, _)
        actual == cachedResponse
    }

    def "TestGetCategories should fetch from database and cache when cache is empty"() {
        given:
        def parentCategory = random.nextObject(CategoryEntity)
        parentCategory.parent = null

        def childCategory = random.nextObject(CategoryEntity)
        childCategory.parent = parentCategory

        def categories = [parentCategory, childCategory]

        when:
        def actual = categoryService.getCategories()

        then:
        1 * cacheUtil.getBucket(CATEGORY_CACHE_KEY) >> null
        1 * categoryRepository.findAll() >> categories
        1 * cacheUtil.saveToCache(CATEGORY_CACHE_KEY, _, CACHE_EXPIRATION_HOURS, HOURS)

        actual != null
        actual.size() == 1
        verifyAll(actual[0]) {
            id == parentCategory.id
            name == parentCategory.name
            slug == parentCategory.slug
            categoryStatus == parentCategory.status
            sortOrder == parentCategory.sortOrder
            children.size() == 1
            verifyAll(children[0]) {
                id == childCategory.id
                name == childCategory.name
                slug == childCategory.slug
                categoryStatus == childCategory.status
                sortOrder == childCategory.sortOrder
            }
        }
    }

    def "TestGetCategories should return empty list when no categories exist"() {
        given:
        def categories = []

        when:
        def actual = categoryService.getCategories()

        then:
        1 * cacheUtil.getBucket(CATEGORY_CACHE_KEY) >> null
        1 * categoryRepository.findAll() >> categories
        1 * cacheUtil.saveToCache(CATEGORY_CACHE_KEY, _, CACHE_EXPIRATION_HOURS, HOURS)
        actual != null
        actual.isEmpty()
    }
}
