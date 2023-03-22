package com.example.LoginPage.service;

import com.example.LoginPage.entity.User;
import com.example.LoginPage.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);
}
