package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/vignettes")
public class VignetteController {

    private final UserService userService;
    private final CarService carService;
    private final VignetteService vignetteService;

    @Autowired
    public VignetteController(UserService userService, CarService carService, VignetteService vignetteService) {
        this.userService = userService;
        this.carService = carService;
        this.vignetteService = vignetteService;
    }

    @GetMapping("/add")
    public String getVignetteAddView(Model model, HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("vignetteAdd")) {
            model.addAttribute("vignetteAdd", new VignetteAddBindingModel(null, null, null));
        }

        UUID id = (UUID) session.getAttribute("user_id");

        UserWithCarsInfoAddServiceView user = userService.getUserWithCarsInfoAddServiceView(id);

        model.addAttribute("user", user);

        return "vignette-add";
    }

    @GetMapping("/add/{id}")
    public String getVignetteAddWithVehicleInformation(Model model, HttpSession session, @PathVariable UUID id) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (id.toString().isBlank()) {
            return "redirect:/vignettes/add";
        }

        if (!model.containsAttribute("vignetteAdd")) {
            model.addAttribute("vignetteAdd", new VignetteAddBindingModel(null, null, null));
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        UserWithCarsInfoAddServiceView user = userService.getUserWithCarsInfoAddServiceView(userId);

        CarVignetteAddServiceView car = carService.getCarVignetteAddServiceView(id);

        model.addAttribute("user", user);
        model.addAttribute("carInfo", car);

        return "vignette-add-with-selected-vehicle";
    }

    @PostMapping("/add/{carId}")
    public String postVignetteAdd(@PathVariable UUID carId,
                                  @Valid VignetteAddBindingModel vignetteAdd,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {

        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vignetteAdd", bindingResult);
            redirectAttributes.addFlashAttribute("vignetteAdd", vignetteAdd);
            return "redirect:/vignettes/add/" + carId;
        }

        vignetteService.doAdd(vignetteAdd, carId);


        return "redirect:/vehicles/" + carId;
    }


}
