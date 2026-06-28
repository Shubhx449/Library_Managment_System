package menu;

import controller.LoginController;
import model.User;
import util.InputUtil;

public class MainMenu {

    private final LoginController loginController = new LoginController();

    public void show() {
        while (true) {
            printMenu();
            int choice = InputUtil.readIntInRange("  Enter Choice: ", 1, 2);
            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> { System.out.println("\n  Thank you. Goodbye!\n"); return; }
            }
        }
    }

    private void handleLogin() {
        User user = loginController.handleLogin();
        if (user == null) {
            InputUtil.pressEnterToContinue();
            return;
        }

        switch (user.getRole()) {
            case "ADMIN"     -> new menu.AdminMenu(user).show();
            case "LIBRARIAN" -> new menu.LibrarianMenu(user).show();
            case "STUDENT"   -> new menu.StudentMenu(user).show();
            default          -> System.out.println("  [!] Unknown role: " + user.getRole());
        }
    }

    private void printMenu() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║    LIBRARY MANAGEMENT SYSTEM         ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println("  1. Login");
        System.out.println("  2. Exit");
        System.out.println("  ────────────────────────────────────────");
    }
}
