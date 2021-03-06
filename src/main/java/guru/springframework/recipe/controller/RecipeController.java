package guru.springframework.recipe.controller;

import guru.springframework.recipe.commands.RecipeCommand;
import guru.springframework.recipe.exception.NotFoundException;
import guru.springframework.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * @author Krzysztof Kukla
 */
@Controller
@RequestMapping(value = "recipe")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {
    public static final String RECIPE_RECIPE_FORM_URL = "recipe/recipeForm";
    private final RecipeService recipeService;

    @GetMapping("/new")
    public String showById(Model model) {
        model.addAttribute("recipe", new RecipeCommand());

        return RECIPE_RECIPE_FORM_URL;
    }

    @GetMapping(value = "/{id}/show")
    public String findById(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/show";
    }

    @GetMapping("/{id}/update")
    public String updateRecipe(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipeService.findRecipeCommandById(id));
        return RECIPE_RECIPE_FORM_URL;
    }

    @PostMapping
    //@ModelAttribute allows to bind form post parameters to RecipeCommand object
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand recipe, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPE_FORM_URL;
        }
        RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(recipe);
        return "redirect:/recipe/" + savedRecipeCommand.getId() + "/show";
    }

    @GetMapping("/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        log.debug("Deleting id-> " + id);
        recipeService.deleteById(id);
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleException(Exception exception) {
        log.error("Handling not found exception");
        log.error(exception.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);
        return modelAndView;
    }

}
