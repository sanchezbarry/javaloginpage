package com.example.LoginPage.event.listener;

import com.example.LoginPage.entity.User;
import com.example.LoginPage.event.RegistrationCompleteEvent;
import com.example.LoginPage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.UUID;
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //creates verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);


        //send mail to user (sudo print, so we don't have to create the email flow)
        String url = event.getApplicationUrl() + "verifyRegistration?token=" + token;

        //send verification (fake)
        log.info("Click the link to verify your account: {}",url);
    }
}
