package guru.springframework.recipe.controller;

import guru.springframework.recipe.commands.RecipeCommand;
import guru.springframework.recipe.domain.Recipe;
import guru.springframework.recipe.service.RecipeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    void findById() throws Exception {
        Long id = 1L;
        Recipe recipe = Recipe.builder().id(id).build();

        BDDMockito.when(recipeService.findById(id)).thenReturn(recipe);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/show/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("recipe/show"))
            .andExpect(MockMvcResultMatchers.model().attribute("recipe", Matchers.any(Recipe.class)));
        BDDMockito.then(recipeService).should().findById(ArgumentMatchers.anyLong());
    }

    @Test
    void showById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/new"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("recipe", Matchers.any(RecipeCommand.class)))
            .andExpect(MockMvcResultMatchers.view().name("/recipe/recipeForm"));
    }

    @Test
    void saveOrUpdate() throws Exception {
        RecipeCommand recipeCommand = RecipeCommand.builder().id(1L).build();

        BDDMockito.when(recipeService.saveRecipeCommand(ArgumentMatchers.any(RecipeCommand.class))).thenReturn(recipeCommand);

        mockMvc.perform(MockMvcRequestBuilders.post("/recipe"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:/recipe/show/" + recipeCommand.getId()));
        BDDMockito.then(recipeService).should().saveRecipeCommand(ArgumentMatchers.any(RecipeCommand.class));
    }

}