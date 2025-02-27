package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.web.dto.VehicleAddBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

}
