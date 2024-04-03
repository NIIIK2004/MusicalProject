package com.example.controller;

import com.example.impl.UserImpl;
import com.example.model.Subscription;
import com.example.model.User;
import com.example.repo.SubscriptionRepo;
import com.example.repo.UserRepo;
import com.example.validator.RegistrationValidator;
import com.example.validator.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    @Value("${upload.path}")
    private String uploadPath;
    private final UserImpl userImpl;
    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;

    private final UserValidator userValidator;

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(userValidator);
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?logout";
        }
        String username = principal.getName();
        User user = userImpl.findByUsername(username);
        if (user == null) {
            return "redirect:/login?logout";
        }
        List<Subscription> subscription = subscriptionRepo.getAllSubscriptionsByUser(user);
        model.addAttribute("subscription", subscription);
        model.addAttribute("user", user);
        return "Profile";
    }

    @GetMapping("/setting")
    public String settingUser(Model model, Principal principal) {
        String username = principal.getName();
        User user = userImpl.findByUsername(username);
        if (user == null) {
            return "redirect:/login?logout";
        }
        model.addAttribute("user", user);
        return "Setting";
    }

    @PostMapping("/setting/save")
    public String settingUserSave(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "Setting";
        }

        Optional<User> optionalUser = userRepo.findById(user.getId());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            if (file != null) {
                if (file.isEmpty()) {
                    user.setAvatar(existingUser.getAvatar());
                } else {
                    try {
                        String avatar = UUID.randomUUID().toString() + ".jpg";
                        file.transferTo(new File(uploadPath + "/" + avatar));
                        user.setAvatar(avatar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            userImpl.updateFields(user.getId(), user.getName(), user.getSurname(), user.getMail(), user.getUsername(), user.getPassword(), user.getAvatar());
            redirectAttributes.addFlashAttribute("message", "Данные пользователя изменены");
            return "redirect:/profile";
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping("/setting/validate")
    @ResponseBody
    public List<String> validateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
        }
        return errors;
    }

}