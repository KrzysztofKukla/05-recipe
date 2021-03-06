package guru.springframework.recipe.controller;

import guru.springframework.recipe.commands.RecipeCommand;
import guru.springframework.recipe.controller.exceptionhandler.ControllerExceptionHandler;
import guru.springframework.recipe.domain.Recipe;
import guru.springframework.recipe.exception.NotFoundException;
import guru.springframework.recipe.service.RecipeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Krzysztof Kukla
 */
@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @InjectMocks
    private RecipeController recipeController;

    @Mock
    private RecipeService recipeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController)
            .setControllerAdvice(ControllerExceptionHandler.class)
            .build();
    }

    @Test
    void findById() throws Exception {
        Long id = 1L;
        Recipe recipe = Recipe.builder().id(id).build();

        BDDMockito.when(recipeService.findById(id)).thenReturn(recipe);

        mockMvc.perform(get("/recipe/1/show"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("recipe/show"))
            .andExpect(MockMvcResultMatchers.model().attribute("recipe", Matchers.any(Recipe.class)));
        BDDMockito.then(recipeService).should().findById(anyLong());
    }

    @Test
    void findByIdNotExist() throws Exception {
        BDDMockito.when(recipeService.findById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/recipe/1/show"))
            .andExpect(status().isNotFound());
    }

    @Test
    void showById() throws Exception {
        mockMvc.perform(get("/recipe/new"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("recipe", Matchers.any(RecipeCommand.class)))
            .andExpect(MockMvcResultMatchers.view().name("recipe/recipeForm"));
    }

    @Test
    void updateRecipeTest() throws Exception {
        long id = 1L;
        RecipeCommand recipeCommand = RecipeCommand.builder().id(id).description("description").build();

        BDDMockito.when(recipeService.findRecipeCommandById(id)).thenReturn(recipeCommand);

        mockMvc.perform(get("/recipe/1/update"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("recipe", Matchers.any(RecipeCommand.class)))
            .andExpect(MockMvcResultMatchers.view().name("recipe/recipeForm"));
        BDDMockito.then(recipeService).should().findRecipeCommandById(anyLong());
    }

    @Test
    void deleteById() throws Exception {

        mockMvc.perform(get("/recipe/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        BDDMockito.then(recipeService).should().deleteById(anyLong());
    }

    @Test
    void handleExceptionTest() throws Exception {
        BDDMockito.when(recipeService.findById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/recipe/1/show"))
            .andExpect(status().isNotFound())
            .andExpect(view().name("404error"));
        BDDMockito.then(recipeService).should(BDDMockito.times(1)).findById(anyLong());
    }

    @Test
    void numberFormatExceptionHandlerTest() throws Exception {
        mockMvc.perform(get("/recipe/ssss/show"))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("400error"));
        BDDMockito.then(recipeService).shouldHaveNoInteractions();
    }

    @Test
    void saveOrUpdateTestHasValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/recipe")
            .param("directions", "some directions")
            .param("cookTime", "3000")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("recipe", "description"))
            .andExpect(model().attributeHasFieldErrors("recipe", "cookTime"))
            .andExpect(view().name("recipe/recipeForm"));
        BDDMockito.then(recipeService).shouldHaveNoInteractions();
    }

    @Test
    void saveOrUpdateTestNoValidationErrors() throws Exception {
        RecipeCommand recipeCommand = RecipeCommand.builder().id(1L).build();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("description", "abc");
        params.add("directions", "some direction");

        BDDMockito.when(recipeService.saveRecipeCommand(any(RecipeCommand.class))).thenReturn(recipeCommand);

        mockMvc.perform(post("/recipe")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .params(params)
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/recipe/1/show"));
        BDDMockito.then(recipeService).should().saveRecipeCommand(any(RecipeCommand.class));
    }

}