package com.smart.controller;

import java.net.InetAddress;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Show the form to enter email
    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forget_email_form";
    }

    // Handle send OTP request
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {

        System.out.println("Email: " + email);

        // ✅ Check if email domain is valid
        if (!isDomainValid(email)) {
            session.setAttribute("message", "Invalid email domain. Please enter a valid email.");
            session.setAttribute("otpSent", false);
            return "forget_email_form";
        }

        // ✅ Generate OTP only if domain is valid
        Random random = new Random();
        int otp = random.nextInt(999999);
        System.out.println("OTP: " + otp);

        String subject = "OTP from Smart Contact Manager";
        String message = "Dear User,\n\nYour OTP is: " + otp + "\n\nRegards,\nSmart Contact Team";

        boolean sent = emailService.sendEmail(email, subject, message);

        if (sent) {
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);
            session.setAttribute("otpSent", true);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Check your email ID. Email sending failed.");
            session.setAttribute("otpSent", false);
            return "forget_email_form";
        }
    }
    
    //verify otp
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp
    		,HttpSession session) {
    	int myOtp=(int)session.getAttribute("myotp");
    	String email=(String)session.getAttribute("email");
    	if(myOtp==otp) {
    		
    		User user=this.userRepository.getUserByUserName(email);
    		if(user==null) {
    			//send error message
    			   session.setAttribute("message", "User does not exists with this email..");
    	            session.setAttribute("otpSent", false);
    	            return "forget_email_form";
    		}
    		else {
    			//send change password form
    		}
    		return "password_change_form";
    	}
    	else {
    		 session.setAttribute("message", "You have entered wrong otp..");
    		 return "verify_otp";
    	}
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);

        if (user != null) {
            user.setPassword(this.passwordEncoder.encode(newpassword)); // ✅ Corrected line
            this.userRepository.save(user);

            session.removeAttribute("email");
            session.removeAttribute("myotp");
           
            return "redirect:/signin?change=password changed successfully";
        } else {
            session.setAttribute("message", "Something went wrong. Please try again.");
            return "forget_email_form";
        }
    }


    // ✅ Utility: Check if domain part of email is valid
    private boolean isDomainValid(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);
            InetAddress.getByName(domain);  // Resolve domain
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
