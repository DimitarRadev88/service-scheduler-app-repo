package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginView(Model model, HttpSession session) {
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }

        if (!model.containsAttribute("userLogin")) {
            UserLoginBindingModel userLogin = new UserLoginBindingModel((String) model.getAttribute("email"), null);
            model.addAttribute("userLogin", userLogin);
        }

        return "login";
    }

    @PostMapping("/login")
    public ModelAndView postUserLogin(ModelAndView modelAndView,
                                      @Valid UserLoginBindingModel userLogin,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      HttpSession session
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userLogin", userLogin);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userLogin", bindingResult);
            modelAndView.setViewName("redirect:/login");
        } else {
            UUID userId = userService.doLogin(userLogin);
            modelAndView.setViewName("redirect:/home");
            session.setAttribute("user_id", userId);
        }


        return modelAndView;
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
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegister", userRegister);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegister", bindingResult);
            modelAndView.setViewName("redirect:/register");
        } else {
            String email = userService.doRegister(userRegister);
            modelAndView.setViewName("redirect:/");
            redirectAttributes.addFlashAttribute("email", email);
        }


        return modelAndView;
    }

}
