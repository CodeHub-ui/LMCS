package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.model.Admin;
import com.library.model.Session;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AdminDAO adminDAO = new AdminDAO();

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            Admin admin = adminDAO.login(username, password);
            if (admin != null) {
                Session adminSession = new Session(admin.getId(), admin.getName(), admin.getEmail());
                session.setAttribute("adminSession", adminSession);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
