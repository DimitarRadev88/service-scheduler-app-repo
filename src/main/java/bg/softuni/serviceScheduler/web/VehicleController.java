package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carModel.service.CarModelService;
import bg.softuni.serviceScheduler.carServices.oilChange.service.OilChangeService;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInfoServiceViewModel;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final CarService carService;
    private final CarModelService carModelService;
    private final OilChangeService oilChangeService;

    public VehicleController(CarService carService, CarModelService carModelService, OilChangeService oilChangeService) {
        this.carService = carService;
        this.carModelService = carModelService;
        this.oilChangeService = oilChangeService;
    }

    @GetMapping("/add")
    public String getVehicleAddPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addUserId(userDetails, model);

        model.addAttribute("brands", carModelService.getAllBrands());

        return "vehicle-add";
    }

    @GetMapping("/add/{brand}")
    public String getVehicleAddPageWithBrand(@AuthenticationPrincipal UserDetails userDetails, Model model, @PathVariable String brand) {
        addUserId(userDetails, model);

        if (!model.containsAttribute("vehicleAdd")) {
            CarAddBindingModel vehicleAdd = new CarAddBindingModel(brand);
            model.addAttribute("vehicleAdd", vehicleAdd);
        }

        model.addAttribute("brands", carModelService.getAllBrands());
        model.addAttribute("models", carModelService.getAllModelsByBrand(brand));

        return "vehicle-add-with-brand";
    }


    @PostMapping("/add/{brand}")
    public ModelAndView addNewVehicleWithBrand(@AuthenticationPrincipal UserDetails userDetails,
                                               ModelAndView modelAndView,
                                               @PathVariable String brand,
                                               @Valid CarAddBindingModel vehicleAdd,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors() || !vehicleAdd.brand().equals(brand)) {
            redirectAttributes.addFlashAttribute("vehicleAdd", vehicleAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vehicleAdd", bindingResult);
            modelAndView.setViewName("redirect:/vehicles/add/" + brand);
        } else {
            UUID id = carService.doAdd(vehicleAdd, ((ServiceSchedulerUserDetails) userDetails).getId());
            modelAndView.setViewName("redirect:/vehicles/" + id);
        }

        return modelAndView;
    }

    @GetMapping("/{id}")
    public String getVehicleDetails(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, Model model) {
        addUserId(userDetails, model);

        CarInfoServiceViewModel carInfo = carService.getCarInfoServiceViewModel(id);

        if (!model.containsAttribute("engineMileageAdd")) {
            model.addAttribute("engineMileageAdd", new EngineMileageAddBindingModel(carInfo.engine().mileage(), carInfo.engine().mileage()));
        }

        model.addAttribute("carInfo", carInfo);

        return "vehicle-info";
    }

    @PutMapping("/{id}/add-mileage")
    public String changeVehicleMileage(@PathVariable UUID id,
                                       @Valid EngineMileageAddBindingModel engineMileageAdd,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("engineMileageAdd", engineMileageAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.engineMileageAdd", bindingResult);
        } else {
            carService.doAddMileage(engineMileageAdd, id);
        }

        return "redirect:/vehicles/" + id;
    }

    @GetMapping("/engines/{id}/oil-changes/add")
    public String getAddOilChangeView(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, Model model) {
        addUserId(userDetails, model);

        model.addAttribute("engineView", carService.getEngineOilChangeAddViewModel(id));

        if (!model.containsAttribute("oilChangeAdd")) {
            model.addAttribute("oilChangeAdd", new OilChangeAddBindingModel(LocalDate.now(), null, null, null));
        }

        return "vehicle-oil-change-add";
    }

    private static void addUserId(UserDetails userDetails, Model model) {
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            model.addAttribute("userId", ((ServiceSchedulerUserDetails) userDetails).getId());
        }
    }

    @PostMapping("/engines/{id}/oil-changes/add")
    public String addOilChangePost(
            @PathVariable UUID id,
            @Valid OilChangeAddBindingModel oilChangeAdd,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.oilChangeAdd", bindingResult);
            redirectAttributes.addFlashAttribute("oilChangeAdd", oilChangeAdd);
            return "redirect:/vehicles/engines/" + id + "/oil-changes/add";
        } else {
            UUID carId = oilChangeService.doAdd(oilChangeAdd, id);
            return "redirect:/vehicles/" + carId;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteVehicle(@PathVariable UUID id) {
        carService.doDelete(id);
        return "redirect:/";
    }

    @GetMapping("/services")
    public String getAllServicesView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UUID userId = ((ServiceSchedulerUserDetails) userDetails).getId();
        model.addAttribute("userId", userId);
        List<CarDashboardServicesDoneViewServiceModel> services = carService.getAllServicesByUser(userId);
        BigDecimal allServicesCost = carService.getAllServicesCostByUser(userId);

        model.addAttribute("services", services);
        model.addAttribute("allServicesCost", allServicesCost);

        return "vehicle-all-services";
    }

}
