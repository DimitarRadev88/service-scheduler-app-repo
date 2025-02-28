package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.web.dto.VehicleAddBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final CarService carService;

    public VehicleController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/add")
    public String getVehicleAddPage(Model model) {
        if (!model.containsAttribute("vehicleAdd")) {
            model.addAttribute("vehicleAdd", new VehicleAddBindingModel());
        }

        if (!model.containsAttribute("brands")) {
            Map<String, List<String>> allBrandsWithModels = carService.getAllBrandsWithModels();
            model.addAttribute("brands", allBrandsWithModels);
        }

        return "vehicle-add";
    }

    @GetMapping("/add/{brand}")
    public String getVehicleAddPageWithBrand(Model model, @PathVariable String brand) {

        VehicleAddBindingModel vehicleAdd = new VehicleAddBindingModel(brand);
        model.addAttribute("vehicleAdd", vehicleAdd);

        Map<String, List<String>> brandsWithModels = carService.getAllBrandsWithModels();

        if (!model.containsAttribute("brands")) {
            model.addAttribute("brands", brandsWithModels);
        }
        if (!model.containsAttribute("models")) {
            model.addAttribute("models", brandsWithModels.get(brand));
        }

        return "vehicle-add";
    }

    @PostMapping("/add")
    public ModelAndView addNewVehicle(ModelAndView modelAndView,
                                      @Valid VehicleAddBindingModel vehicleAdd,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("vehicleAdd", vehicleAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vehicleAdd", bindingResult);
            modelAndView.setViewName("redirect:/vehicles/add");
        } else {
            carService.doAdd(vehicleAdd, (UUID) session.getAttribute("user_id"));
            modelAndView.setViewName("redirect:/vehicles/{id}");
        }

        return modelAndView;
    }

    @PostMapping("/add/{brand}")
    public ModelAndView addNewVehicleWithBrand(ModelAndView modelAndView,
                                               @PathVariable String brand,
                                               @Valid VehicleAddBindingModel vehicleAdd,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes,
                                               HttpSession session) {

        if (bindingResult.hasErrors() || !vehicleAdd.make().equals(brand)) {
            redirectAttributes.addFlashAttribute("vehicleAdd", vehicleAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vehicleAdd", bindingResult);
            modelAndView.setViewName("redirect:/vehicles/add");
        } else {
            carService.doAdd(vehicleAdd, (UUID) session.getAttribute("user_id"));
            modelAndView.setViewName("redirect:/vehicles/{id}");
        }

        return modelAndView;
    }

}
