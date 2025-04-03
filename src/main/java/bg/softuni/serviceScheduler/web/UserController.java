package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.AllUsersServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserEditProfileServiceModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginView() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            return "redirect:/";
        }

        if (!model.containsAttribute("userRegister")) {
            model.addAttribute("userRegister", new UserRegisterBindingModel());
        }

        return "register";
    }

    @PostMapping("/register")
    public ModelAndView postUserRegister(ModelAndView modelAndView,
                                         @Valid UserRegisterBindingModel userRegister,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes
    ) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegister", userRegister);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegister", bindingResult);
            modelAndView.setViewName("redirect:/register");
        } else {
            String username = userService.doRegister(userRegister);
            modelAndView.setViewName("redirect:/login");
            redirectAttributes.addFlashAttribute("username", username);
        }

        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public String getUsersView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UUID id = ((ServiceSchedulerUserDetails) userDetails).getId();

        model.addAttribute("userId", id);
        List<AllUsersServiceModelView> users = userService.getAllUsersWithout(id);
        model.addAttribute("users", users);


        return "all-users";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/users/change-role/user/{id}")
    public String removeAdminRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.removeAdmin(id);

        redirectAttributes.addFlashAttribute("message",  id + " has been demoted to user");

        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/users/change-role/admin/{id}")
    public String makeAdmin(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.makeAdmin(id);

        redirectAttributes.addFlashAttribute("message",  id + " has been promoted to admin");

        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String getProfileView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            UUID id = ((ServiceSchedulerUserDetails) userDetails).getId();
            model.addAttribute("userId", id);
            model.addAttribute("user", userService.getUserProfileView(id));
        }

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String getProfileEditView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        UUID id = null;
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            id = ((ServiceSchedulerUserDetails) userDetails).getId();
            model.addAttribute("userId", id);
        }

        UserEditProfileServiceModel user = userService.getUserEditProfileServiceModel(id);
        model.addAttribute("user", userService.getUserProfileView(id));
        model.addAttribute("profilePicture", user.profilePictureUrl());

        if (!model.containsAttribute("userEdit")) {
            model.addAttribute("userEdit", new UserProfileEditBindingModel(user.profilePictureUrl()));
        }

        return "profile-edit";
    }

    @PutMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable UUID id,
                              @Valid UserProfileEditBindingModel userProfileEditBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userEdit", bindingResult);
            redirectAttributes.addFlashAttribute("userEdit", userProfileEditBindingModel);
            return "redirect:/profile/edit";
        }

        String message = userService.doEdit(userProfileEditBindingModel, id);

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/profile";
    }

}
