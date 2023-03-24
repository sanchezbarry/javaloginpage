package com.example.LoginPage.service;

import com.example.LoginPage.entity.User;
import com.example.LoginPage.entity.VerificationToken;
import com.example.LoginPage.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetToken(User user, String token);

    String validatePasswordToken(String token);

    Optional<User> getUserByPasswordToken(String token);
}
