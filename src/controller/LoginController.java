package controller;

import exception.AuthenticationException;
import model.User;
import service.AuthenticationService;
import util.InputUtil;

public class LoginController {

    private final AuthenticationService authService = new AuthenticationService();


    public User handleLogin() {
        System.out.println("\n  ──────────────────────────────────");
        System.out.println("            USER LOGIN              ");
        System.out.println("  ──────────────────────────────────");

        String email    = InputUtil.readString("  Email    : ");
        String password = InputUtil.readPassword("  Password : ");

        try {
            User user = authService.login(email, password);
            System.out.println("\n  [✓] Login successful! Welcome, " + user.getName());
            return user;
        } catch (AuthenticationException e) {
            System.out.println("\n  [✗] Login failed: " + e.getMessage());
            return null;
        }
    }
}
