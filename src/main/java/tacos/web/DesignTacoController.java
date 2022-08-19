package tacos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.JpaIngredientRepository;
import tacos.data.JpaTacoRepository;
import tacos.data.TacoRepository;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    @ModelAttribute(name="order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name="taco")
    public Taco taco() {
        return new Taco();
    }

    private final IngredientRepository ingredientRepository;

    private final JpaIngredientRepository jpaIngredientRepository;

    private TacoRepository tacoRepository;

    private final JpaTacoRepository jpaTacoRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository,
                                JpaIngredientRepository jpaIngredientRepository, JpaTacoRepository jpaTacoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
        this.jpaIngredientRepository = jpaIngredientRepository;
        this.jpaTacoRepository = jpaTacoRepository;
    }

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
//        ingredientRepository.findAll().forEach(ingredient -> ingredients.add(ingredient));
        jpaIngredientRepository.findAll().forEach(ingredient -> ingredients.add(ingredient));
        log.info("Loading ingredients from DB: " + ingredients.size());
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("design", new Taco());

        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream().filter(ingredient -> ingredient.getType() == type).collect(Collectors.toList());
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            return "design";
        }

//        Taco saved = tacoRepository.save(design);
        log.info("Taco: " + design.toString());
        Taco saved = jpaTacoRepository.save(design);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }
}
