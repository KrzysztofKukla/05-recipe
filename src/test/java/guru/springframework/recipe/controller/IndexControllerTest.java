package guru.springframework.recipe.controller;

import guru.springframework.recipe.domain.Category;
import guru.springframework.recipe.domain.Recipe;
import guru.springframework.recipe.domain.UnitOfMeasure;
import guru.springframework.recipe.service.CategoryService;
import guru.springframework.recipe.service.RecipeService;
import guru.springframework.recipe.service.UnitOfMeasureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Krzysztof Kukla
 */
class IndexControllerTest {

    private IndexController indexController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UnitOfMeasureService unitOfMeasureService;

    @Mock
    private RecipeService recipeService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(categoryService, unitOfMeasureService, recipeService);
    }

    @Test
    void index() {
        Recipe first = Recipe.builder().id(1L).description("first Descriptuion").build();
        Recipe second = Recipe.builder().id(2L).description("second Descriptuion").build();
        Recipe third = Recipe.builder().id(3L).description("third Descriptuion").build();
        Set<Recipe> recipes = new HashSet<>(Arrays.asList(first, second, third));

        String categoryDescription = "Italian";
        Category category = Category.builder().description(categoryDescription).build();

        String unitOfMeasureDescription = "Cup";
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder().description(unitOfMeasureDescription).build();

        BDDMockito.when(recipeService.findAll()).thenReturn(recipes);
        BDDMockito.when(categoryService.findByDescription(categoryDescription)).thenReturn(category);
        BDDMockito.when(unitOfMeasureService.findByDescription(unitOfMeasureDescription)).thenReturn(unitOfMeasure);

        Assertions.assertEquals("index", indexController.index(model));
        BDDMockito.then(recipeService).should().findAll();
        BDDMockito.then(categoryService).should().findByDescription(ArgumentMatchers.anyString());
        BDDMockito.then(unitOfMeasureService).should().findByDescription(ArgumentMatchers.anyString());
        BDDMockito.then(model).should(BDDMockito.times(1))
            .addAttribute(ArgumentMatchers.matches("recipes"), ArgumentMatchers.anySet());

    }

}