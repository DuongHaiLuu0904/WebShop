package com.ptit.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ptit.entity.Customers;
import com.ptit.entity.Category;
import com.ptit.service.CustomerService;
import com.ptit.service.MailerService;
import com.ptit.service.CategoryService;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {    
    @Autowired
    CustomerService customerService;

    @Autowired
    @Qualifier("mailerService")
    MailerService mailerService;

    @Autowired
    CategoryService categoryService;

    @CrossOrigin("*")
    @ResponseBody
    @RequestMapping("/rest/auth/authentication")
    public Object getAuthentication(HttpSession session) {
        return session.getAttribute("authentication");
    }    @RequestMapping("/auth/login/form")
    
    public String logInForm(Model model) {
        // Load categories for menu
        List<Category> cates = categoryService.findAll();
        model.addAttribute("cates", cates);
        return "auth/login";
    }
    
    @RequestMapping("/auth/login/test")
    public String loginTest(Model model) {
        return "auth/login-test";
    }

    @RequestMapping("/auth/login/success")
    public String logInSuccess(Model model) {
        model.addAttribute("message", "Logged in successfully");
        return "redirect:/index";
    }

    @RequestMapping("/auth/login/error")
    public String logInError(Model model) {
        model.addAttribute("message", "Wrong login information!");
        return "auth/login";
    }

    @RequestMapping("/auth/unauthoried")
    public String unauthoried(Model model) {
        model.addAttribute("message", "You don't have access!");
        return "auth/login";
    }

    @RequestMapping("/auth/logout/success")
    public String logOutSuccess(Model model) {
        model.addAttribute("message", "You are logged out!");
        return "auth/login";
    }

    // OAuth2
    @RequestMapping("/oauth2/login/success")
    public String oauth2(OAuth2AuthenticationToken oauth2, HttpSession session) {
        customerService.loginFromOAuth2(oauth2);
        
        // Lấy thông tin customer sau khi đã được tạo/cập nhật trong database
        String email = oauth2.getPrincipal().getAttribute("email");
        
        Customers customer = customerService.findByEmail(email);
        
        if (customer != null) {
            // Tạo authentication map và lưu vào session như trong AuthenticationInterceptor
            Map<String, Object> authMap = new HashMap<>();
            authMap.put("user", customer);
            byte[] token = (customer.getUsername() + ":" + customer.getPassword()).getBytes();
            authMap.put("token", "Basic " + Base64.getEncoder().encodeToString(token));
            session.setAttribute("authentication", authMap);
        } else {
            System.out.println("Customer not found after OAuth2 login!");
        }
        return "forward:/auth/login/success";
    }

    @GetMapping("/auth/register")
    public String signUpForm(Model model) {
        model.addAttribute("customer", new Customers());
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String signUpSuccess(Model model, @Validated @ModelAttribute("customer") Customers customer, 
                                Errors error, HttpServletResponse response) {
        if (error.hasErrors()) {
            model.addAttribute("message", "Please correct the error below!");
            return "auth/register";
        }
        customer.setPhoto("user.png");
        customer.setToken("token");
        customerService.create(customer);
        model.addAttribute("message", "New account registration successful!");
        response.addHeader("refresh", "2;url=/auth/login/form");
        return "auth/register";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordForm(Model model) {
        return "auth/forgot-password";
    }

    @PostMapping("/auth/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request, Model model)
            throws Exception {
        try {
            String token = RandomString.make(50);
            customerService.updateToken(token, email);
            String resetLink = getSiteURL(request) + "/auth/reset-password?token=" + token;
            mailerService.sendEmail(email, resetLink);
            model.addAttribute("message", "We have sent a reset password link to your email. "
                    + "If you don't see the email, check your spam folder.");
        } catch (MessagingException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error while sending email");
        }
        return "auth/forgot-password";
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordForm(@Param(value = "token") String token, Model model) {
        Customers customer = customerService.getByToken(token);
        model.addAttribute("token", token);
        if (customer == null) {
            model.addAttribute("message", "Invalid token!");
            return "redirect:/auth/login/form";
        }
        return "auth/reset-password";
    }

    @PostMapping("/auth/reset-password")
    public String processResetPassword(@RequestParam("token") String code, @RequestParam("password") String password,
                                       HttpServletResponse response, Model model) {
        Customers token = customerService.getByToken(code);
        if (token == null) {
            model.addAttribute("message", "Invalid token!");
        } else {
            customerService.updatePassword(token, password);
            model.addAttribute("message", "You have successfully changed your password!");
            response.addHeader("refresh", "2;url=/auth/login/form");
        }
        return "auth/reset-password";
    }

    @GetMapping("/auth/change-password")
    public String changePasswordForm(Model model) {
        return "auth/change-password";
    }    @PostMapping("/auth/change-password")
    public String processChangePassword(Model model, @RequestParam("username") String username,
                                        @RequestParam("password") String newPassword) {
        Customers customer = customerService.findByUsername(username);
        customerService.changePassword(customer, newPassword);
        model.addAttribute("message", "Change password successfully!");
        return "auth/change-password";
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
