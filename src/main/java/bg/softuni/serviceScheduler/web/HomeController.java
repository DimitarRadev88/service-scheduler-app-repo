package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String viewHome(HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            model.addAttribute("statistics", userService.getStatistics());
            return "index";
        }

        model.addAttribute("user", userService.getUser((UUID) session.getAttribute("user_id")));
        return "home";
    }

}
