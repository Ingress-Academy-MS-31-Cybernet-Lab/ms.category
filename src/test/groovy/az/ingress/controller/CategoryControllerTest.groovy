package az.ingress.controller

import az.ingress.exception.ErrorHandler
import az.ingress.model.request.CategoryRequest
import az.ingress.model.response.CategoryResponse
import az.ingress.service.abstraction.CategoryService
import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CategoryControllerTest extends Specification {
    EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom()
    CategoryService categoryService
    CategoryController categoryController
    MockMvc mockMvc

    def setup() {
        categoryService = Mock()
        categoryController = new CategoryController(categoryService)
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new ErrorHandler())
                .build()
    }

    def "TestCreateCategory: success case"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        1 * categoryService.createCategory(request)
        actual.andExpect(status().isCreated())
    }

    def "TestGetCategories: success case"() {
        given:
        def url = "/v1/categories"

        def childCategory = random.nextObject(CategoryResponse, "children")

        def parentCategory = random.nextObject(CategoryResponse, "children")
        parentCategory.children = [childCategory]

        def jsonResponse = """
                                    [
                                        {
                                          "id": $parentCategory.id,
                                          "name": "$parentCategory.name",
                                          "slug": "$parentCategory.slug",
                                          "categoryStatus": "$parentCategory.categoryStatus",
                                          "sortOrder": $parentCategory.sortOrder,
                                          "children": [
                                            {
                                              "id": $childCategory.id,
                                              "name": "$childCategory.name",
                                              "slug": "$childCategory.slug",
                                              "categoryStatus": "$childCategory.categoryStatus",
                                              "sortOrder": $childCategory.sortOrder,
                                              "children": $childCategory.children
                                            }
                                          ]
                                        }
                                    ]
                                    """

        when:
        def actual = mockMvc.perform(get(url).accept(APPLICATION_JSON))

        then:
        1 * categoryService.getCategories() >> [parentCategory]
        actual.andExpectAll(
                status().isOk(),
                content().json(jsonResponse, true)
        )
    }

    def "TestCreateCategory: validation error when name is blank"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.name = ""
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }

    def "TestCreateCategory: validation error when name is null"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.name = null
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }

    def "TestCreateCategory: validation error when slug is blank"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.slug = ""
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }

    def "TestCreateCategory: validation error when slug is null"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.slug = null
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }

    def "TestCreateCategory: validation error when name is whitespace only"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.name = "   "
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }

    def "TestCreateCategory: validation error when slug is whitespace only"() {
        given:
        def url = "/v1/categories"
        def request = random.nextObject(CategoryRequest)
        request.slug = "   "
        def jsonRequest = """
                                    {
                                      "parentId": $request.parentId,
                                      "name": "$request.name",
                                      "slug": "$request.slug",
                                      "sortOrder": $request.sortOrder
                                    }
                                    """

        when:
        def actual = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))

        then:
        0 * categoryService.createCategory(_)
        actual.andExpect(status().isBadRequest())
    }
}
