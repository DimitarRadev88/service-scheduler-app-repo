package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carModel.service.CarModelService;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.web.dto.CarBrandAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarModelAddBindingModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/models")
public class CarModelController {

    private final CarModelService carModelService;

    @Autowired
    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add")
    public String getAddCarModelView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addUserId(userDetails, model);

        if (!model.containsAttribute("carModelAdd")) {
            model.addAttribute("carModelAdd", new CarModelAddBindingModel(null, null));
        }
        model.addAttribute("brands", carModelService.getAllBrands());

        return "add-car-model";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add/{brand}")
    public String getAddCarModelViewWithBrand(@AuthenticationPrincipal UserDetails userDetails,
                                              Model model, @PathVariable String brand) {
        addUserId(userDetails, model);

        if (!model.containsAttribute("carModelAdd")) {
            model.addAttribute("carModelAdd", new CarModelAddBindingModel(brand, null));
        }
        model.addAttribute("brands", carModelService.getAllBrands());

        return "add-car-model-with-brand";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add/{brand}")
    public String postAddCarModel(@Valid CarModelAddBindingModel carModelAdd, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes, @PathVariable String brand) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("carModelAdd", carModelAdd);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.carModelAdd", bindingResult);
            return "redirect:/models/add/" + brand;
        }

        String carModel = carModelService.doAdd(carModelAdd);

        redirectAttributes.addFlashAttribute("message",  carModel + " added");
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/brands/add")
    public String getAddCarBrandView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addUserId(userDetails, model);

        if (!model.containsAttribute("carBrandAdd")) {
            model.addAttribute("carBrandAdd", new CarBrandAddBindingModel(null));
        }

        return "add-car-brand";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/brands/add")
    public String postAddCarBrand(@Valid CarBrandAddBindingModel carBrandAdd,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("carBrandAdd", carBrandAdd);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.carBrandAdd", bindingResult);
            return "redirect:/models/brands/add";
        }

        carModelService.doAdd(carBrandAdd);

        return "redirect:/models/add/" + carBrandAdd.name();
    }

    private static void addUserId(UserDetails userDetails, Model model) {
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            model.addAttribute("userId", ((ServiceSchedulerUserDetails) userDetails).getId());
        }
    }


}
