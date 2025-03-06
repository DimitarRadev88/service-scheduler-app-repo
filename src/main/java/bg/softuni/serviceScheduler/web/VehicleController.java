package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInfoServiceViewModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.EngineMileageAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
        if (!model.containsAttribute("brands")) {
            Map<String, List<String>> allBrandsWithModels = carService.getAllBrandsWithModels();
            model.addAttribute("brands", allBrandsWithModels);
        }

        return "vehicle-add";
    }

    @GetMapping("/add/{brand}")
    public String getVehicleAddPageWithBrand(Model model, @PathVariable String brand) {
        if (brand.isBlank()) {
            return "redirect:/vehicles/add";
        }

        if (!model.containsAttribute("vehicleAdd")) {
            CarAddBindingModel vehicleAdd = new CarAddBindingModel(brand);
            model.addAttribute("vehicleAdd", vehicleAdd);
        }

        Map<String, List<String>> brandsWithModels = carService.getAllBrandsWithModels();

        if (!model.containsAttribute("brands")) {
            model.addAttribute("brands", brandsWithModels);
        }

        if (!model.containsAttribute("models")) {
            model.addAttribute("models", brandsWithModels.get(brand));
        }

        return "vehicle-add-with-brand";
    }


    @PostMapping("/add/{brand}")
    public ModelAndView addNewVehicleWithBrand(ModelAndView modelAndView,
                                               @PathVariable String brand,
                                               @Valid CarAddBindingModel vehicleAdd,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes,
                                               HttpSession session) {

        if (bindingResult.hasErrors() || !vehicleAdd.make().equals(brand)) {
            redirectAttributes.addFlashAttribute("vehicleAdd", vehicleAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vehicleAdd", bindingResult);
            modelAndView.setViewName("redirect:/vehicles/add/" + brand);
        } else {
            UUID id = carService.doAdd(vehicleAdd, (UUID) session.getAttribute("user_id"));
            modelAndView.setViewName("redirect:/vehicles/" + id);
        }

        return modelAndView;
    }

    @GetMapping("/{id}")
    public String getVehicleDetails(@PathVariable UUID id, Model model) {

        CarInfoServiceViewModel carInfo = carService.getCarInfoServiceViewModel(id);

        if (!model.containsAttribute("engineMileageAdd")) {
            model.addAttribute("engineMileageAdd", new EngineMileageAddBindingModel(carInfo.engine().mileage()));
        }

        model.addAttribute("carInfo", carInfo);

        return "vehicle-info";
    }

    @PostMapping("/{id}/add-mileage")
    public String changeVehicleMileage(@PathVariable UUID id,
                                       @Valid EngineMileageAddBindingModel engineMileageAdd,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session) {

        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("vehicleMileageAdd", engineMileageAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.vehicleMileageAdd", bindingResult);
        } else {
            carService.doAddMileage(engineMileageAdd, id);
        }

            return "redirect:/vehicles/" + id;
    }

    @GetMapping("/engines/{id}/oil-changes/add")
    public String getAddOilChangeView(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("engineView")) {
            model.addAttribute("engineView", carService.getEngineOilChangeAddViewModel(id));
        }

        if (!model.containsAttribute("oilChangeAdd")) {
            model.addAttribute("oilChangeAdd", new OilChangeAddBindingModel(LocalDate.now(), null, null, null));
        }

        return "change-oil";
    }

    @PostMapping("/engines/{id}/oil-changes/add")
    public String addOilChangePost(
            @PathVariable UUID id,
            @Valid OilChangeAddBindingModel oilChangeAdd,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {

        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.oilChangeAdd", bindingResult);
            redirectAttributes.addFlashAttribute("oilChangeAdd", oilChangeAdd);
            return "redirect:/vehicles/engines/" + id + "/oil-changes/add";
        } else {
            UUID carId = carService.doAdd(oilChangeAdd, id);
            return "redirect:/vehicles/" + carId;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteVehicle(@PathVariable UUID id, HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        carService.doDelete(id);
        return "redirect:/";
    }

}
