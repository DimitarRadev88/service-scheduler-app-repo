package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInsuranceAddServiceView;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/insurances")
public class InsuranceController {

    private final UserService userService;
    private final CarService carService;
    private final InsuranceService insuranceService;

    public InsuranceController(UserService userService, CarService carService, InsuranceService insuranceService) {
        this.userService = userService;
        this.carService = carService;
        this.insuranceService = insuranceService;
    }

    @GetMapping("/add")
    public String getInsuranceAddView(Model model, HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("insuranceAdd")) {
            model.addAttribute("insuranceAdd", new InsuranceAddBindingModel(null, null, null, BigDecimal.ZERO));
        }

        UUID id = (UUID) session.getAttribute("user_id");

        UserWithCarsInfoAddServiceView user = userService.getUserWithCarsInfoAddServiceView(id);

        model.addAttribute("user", user);
        return "insurance-add";
    }

    @GetMapping("/add/{id}")
    public String getInsuranceAddWithVehicleInformation(Model model, HttpSession session, @PathVariable UUID id) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (id.toString().isBlank()) {
            return "redirect:/insurances/add";
        }

        if (!model.containsAttribute("insuranceAdd")) {
            model.addAttribute("insuranceAdd", new InsuranceAddBindingModel(null, null, null, BigDecimal.ZERO));
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        UserWithCarsInfoAddServiceView user = userService.getUserWithCarsInfoAddServiceView(userId);

        CarInsuranceAddServiceView car = carService.getCarInsuranceAddServiceView(id);

        model.addAttribute("user", user);
        model.addAttribute("carInfo", car);

        return "insurance-add-with-selected-vehicle";
    }

    @PostMapping("/add/{carId}")
    public String postInsuranceAdd(@PathVariable UUID carId,
                                   @Valid InsuranceAddBindingModel insuranceAdd,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {

        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.insuranceAdd", bindingResult);
            redirectAttributes.addFlashAttribute("insuranceAdd", insuranceAdd);
            return "redirect:/insurances/add/" + carId;
        }

        insuranceService.doAdd(insuranceAdd, carId);

        return "redirect:/vehicles/" + carId;
    }

}
