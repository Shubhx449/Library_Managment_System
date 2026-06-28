package app;

import config.DBConnection;
import menu.MainMenu;

public class Main {

    public static void main(String[] args) {

        try {
            DBConnection.getConnection();
            System.out.println("  [✓] Database connected successfully.");
        } catch (Exception e) {
            System.out.println("  [✗] Database connection failed: " + e.getMessage());
            System.out.println("  Please check DBConnection.java and ensure MySQL is running.");
            System.exit(1);
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
            System.out.println("\n  [✓] Database connection closed.");
        }));


        new MainMenu().show();
    }
}
