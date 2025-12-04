package com.library.controller;

import com.library.dao.StudentDAO;
import com.library.model.Session;
import com.library.model.Student;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentManagementController {

    private final StudentDAO studentDAO = new StudentDAO();

    @GetMapping
    public String listStudents(HttpSession session, Model model) {
        Session adminSession = (Session) session.getAttribute("adminSession");
        if (adminSession == null) {
            return "redirect:/";
        }

        try {
            List<Student> students = studentDAO.getAllStudents(true);
            model.addAttribute("students", students);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load students: " + e.getMessage());
        }

        return "students";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            studentDAO.register(student);
            redirectAttributes.addFlashAttribute("success", "Student added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add student: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/update")
    public String updateStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            studentDAO.updateStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update student: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/delete")
    public String deleteStudent(@RequestParam int id, RedirectAttributes redirectAttributes) {
        try {
            studentDAO.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete student: " + e.getMessage());
        }
        return "redirect:/students";
    }
}
