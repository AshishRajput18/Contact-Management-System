package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());

        // ✅ Remove session message once shown
        session.removeAttribute("message");

        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        try {
            System.out.println("Agreement : " + agreement);
            System.out.println("USER: " + user);

            if (!agreement) {
                throw new Exception("You have not agreed to the terms and conditions");
            }

            // ❌ Validation failed
            if (result.hasErrors()) {
                System.out.println("Validation Errors: " + result);
                model.addAttribute("user", user);
                return "signup";
            }

            // ✅ Encrypt password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // ✅ Set user properties
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("contacts.png"); // ✅ change here

            // ✅ Save user
            User savedUser = userRepository.save(user);
            model.addAttribute("user", new User());

            session.setAttribute("message", new Message("Successfully Registered!!", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "danger"));
        }

        return "signup";
    }
    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model) {
    	model.addAttribute("title", "Login Page");
    	return "login";
    }
}
