package com.library.controller;

import com.library.dao.StudentDAO;
import com.library.model.Student;
import com.library.model.Session;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserLoginController {

    private final StudentDAO studentDAO = new StudentDAO();

    @GetMapping("/")
    public String loginPage() {
        return "user-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        try {
            Student student = studentDAO.authenticate(email, password);
            if (student != null) {
                Session userSession = new Session(student.getId(), student.getName(), student.getEmail());
                session.setAttribute("userSession", userSession);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "user-login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "user-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Session userSession = (Session) session.getAttribute("userSession");
        if (userSession == null) {
            return "redirect:/";
        }

        model.addAttribute("userName", userSession.getName());
        return "user-dashboard";
    }
}
