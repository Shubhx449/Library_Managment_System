package service;

import dao.UserDAO;
import dao.impl.UserDAOImpl;
import exception.AuthenticationException;
import model.User;
import util.PasswordUtil;

import java.util.Optional;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAOImpl();


    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Email and password cannot be empty.");
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("No account found with this email.");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new AuthenticationException("Your account has been deactivated. Contact admin.");
        }

        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect password.");
        }

        return user;
    }


    public void changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("User not found.");
        }

        User user = userOpt.get();

        if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
            throw new AuthenticationException("Current password is incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new AuthenticationException("New passwords do not match.");
        }

        if (newPassword.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters.");
        }

        String hashed = PasswordUtil.hash(newPassword);
        if (!userDAO.updatePassword(userId, hashed)) {
            throw new AuthenticationException("Failed to update password. Try again.");
        }
    }
}
