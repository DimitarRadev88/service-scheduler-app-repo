package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String viewHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails instanceof ServiceSchedulerUserDetails) {
            model.addAttribute("user", userService.getUser(((ServiceSchedulerUserDetails) userDetails).getId()));
            model.addAttribute("userId", ((ServiceSchedulerUserDetails) userDetails).getId());
            return "home";
        } else {
            model.addAttribute("statistics", userService.getStatistics());
            return "index";
        }

    }

}
