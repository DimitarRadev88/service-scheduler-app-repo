package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.AllUsersServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserEditProfileServiceModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.servlet.http.HttpSession;
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
    public String getRegisterView(Model model, HttpSession session) {
        if (session.getAttribute("user_id") != null) {
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
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            model.addAttribute("userId", ((ServiceSchedulerUserDetails) userDetails).getId());
        }

        List<AllUsersServiceModelView> users = userService.getAllUsers();

        model.addAttribute("users", users);

        return "all-users";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/users/change-role/user/{id}")
    public String removeAdminRole(@PathVariable UUID id) {
        userService.removeAdmin(id);

        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/users/change-role/admin/{id}")
    public String makeAdmin(@PathVariable UUID id) {
        userService.makeAdmin(id);

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
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            UUID id = ((ServiceSchedulerUserDetails) userDetails).getId();
            model.addAttribute("userId", id);
            if (!model.containsAttribute("userEdit")) {
                model.addAttribute("user", userService.getUserProfileView(id));
                UserEditProfileServiceModel user = userService.getUserEditProfileServiceModel(id);
                model.addAttribute("profilePicture", user.profilePictureUrl());
                model.addAttribute("userEdit", new UserProfileEditBindingModel(user.username(), user.email(), user.profilePictureUrl()));
            }
        }

        return "profile-edit";
    }

    @PostMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable UUID id,
                              @Valid UserProfileEditBindingModel userProfileEditBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userEdit", bindingResult);
            redirectAttributes.addFlashAttribute("userEdit", userProfileEditBindingModel);
            return "redirect:/profile/edit";
        }

        userService.doEdit(userProfileEditBindingModel, id);

        return "redirect:/profile";
    }

}
