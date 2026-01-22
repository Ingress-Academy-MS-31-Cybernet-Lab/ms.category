package az.ingress.mapper

import az.ingress.dao.entity.CategoryEntity
import az.ingress.model.request.CategoryRequest
import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom
import spock.lang.Specification

import static az.ingress.mapper.CategoryMapper.CATEGORY_MAPPER
import static az.ingress.model.enums.CategoryStatus.ACTIVE

class CategoryMapperTest extends Specification {
    EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom()

    def "TestBuildCategoryEntity with all fields provided"() {
        given:
        def request = random.nextObject(CategoryRequest)

        when:
        def actual = CATEGORY_MAPPER.buildCategoryEntity(request)

        then:
        verifyAll(actual) {
            id == null
            name == request.name
            slug == request.slug
            status == ACTIVE
            sortOrder == request.sortOrder
            parent == null
            createdAt == null
            updatedAt == null
        }
    }

    def "TestBuildCategoryEntity when sortOrder is null"() {
        given:
        def request = random.nextObject(CategoryRequest)
        request.sortOrder = null

        when:
        def actual = CATEGORY_MAPPER.buildCategoryEntity(request)

        then:
        verifyAll(actual) {
            id == null

            name == request.name
            slug == request.slug
            status == ACTIVE
            sortOrder == 0
            parent == null
            createdAt == null
            updatedAt == null
        }
    }

    def "TestToResponseList when parent is null"() {
        given:
        def category = random.nextObject(CategoryEntity)
        category.parent = null

        when:
        def actual = CATEGORY_MAPPER.toResponseList([category])

        then:
        verifyAll(actual[0]) {
            it.id == category.id
            it.name == category.name
            it.slug == category.slug
            it.categoryStatus == category.status
            it.sortOrder == category.sortOrder
            it.children.isEmpty()
        }
    }

    def "TestToResponseList with parent and child categories"() {
        given:
        def parent = random.nextObject(CategoryEntity)
        parent.parent = null

        def child = random.nextObject(CategoryEntity)
        child.parent = parent

        when:
        def actual = CATEGORY_MAPPER.toResponseList([parent, child])

        then:
        verifyAll(actual[0]) {
            it.id == parent.id
            it.name == parent.name
            it.slug == parent.slug
            it.categoryStatus == parent.status
            it.sortOrder == parent.sortOrder
            it.children.size() == 1
        }
        verifyAll(actual[0].children[0]) {
            it.id == child.id
            it.name == child.name
            it.slug == child.slug
            it.categoryStatus == child.status
            it.sortOrder == child.sortOrder
            it.children.isEmpty()
        }
    }

    def "TestToResponseList with empty list"() {
        when:
        def actual = CATEGORY_MAPPER.toResponseList([])

        then:
        actual.isEmpty()
    }
}
