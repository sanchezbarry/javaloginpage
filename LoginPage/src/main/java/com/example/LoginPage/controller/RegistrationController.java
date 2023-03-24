package com.example.LoginPage.controller;

import com.example.LoginPage.entity.User;
import com.example.LoginPage.entity.VerificationToken;
import com.example.LoginPage.event.RegistrationCompleteEvent;
import com.example.LoginPage.model.PasswordModel;
import com.example.LoginPage.model.UserModel;
import com.example.LoginPage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        //saves the user
        User user = userService.registerUser(userModel);
        // event to send the token in the form of a token to verify the user
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";
    }
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")) {
            return "User Verified Successfully";
        }
        return "Bad User";
    }

    //resend token, and replace token in DB with new token
    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent!";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user!=null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(user,token);
            url = passwordRestTokenMail(user,applicationUrl(request), token);
        }
        return url;
    }
    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordToken(token);
        if(!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswordToken(token);
        if(user.isPresent()) {
            return "Password changed successfully";
        } else {
            return "Invalid Token";
        }
    }
    private String passwordRestTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;

        //send verification (fake)
        log.info("Click the link to reset password: {}",url);
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        //send verification (fake)
        log.info("Click the link to verify your account: {}",url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
