package com.example.thuchanh_tuan2.controller;

import com.example.thuchanh_tuan2.model.User;
import com.example.thuchanh_tuan2.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm() {
        return "users/change_password";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String newPassword, HttpSession session, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changePassword(userDetails.getUsername(), newPassword);

        // Invalidate session
        session.invalidate();

        return "redirect:/login?passwordChanged";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "users/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        userService.resetPassword(email);
        model.addAttribute("message", "A password reset link has been sent to your email.");
        return "users/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        if (userService.validateResetToken(token)) {
            model.addAttribute("token", token);
            return "users/reset-password";
        } else {
            model.addAttribute("message", "Invalid password reset token.");
            return "users/login";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password,
                                Model model) {
        if (userService.validateResetToken(token)) {
            userService.updatePassword(token, password);
            model.addAttribute("message", "Password reset successfully. Please log in.");
            return "redirect:/login";
        } else {
            model.addAttribute("message", "Invalid password reset token.");
            return "users/login";
        }
    }
    @GetMapping("/user")
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);
        return "in4";  // Hiển thị thông tin người dùng
    }
}
