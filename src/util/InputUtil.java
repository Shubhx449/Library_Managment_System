package util;

import java.util.Scanner;


public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    private InputUtil() {}

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("  [!] Input cannot be empty. Please try again.");
        }
    }

    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number.");
            }
        }
    }


    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
        }
    }

    public static int readChoice(int max) {
        return readIntInRange("Enter choice: ", 1, max);
    }

    public static String readPassword(String prompt) {
        System.out.print(prompt);
        if (System.console() != null) {
            char[] pw = System.console().readPassword();
            return new String(pw);
        }
        return scanner.nextLine().trim();
    }


    public static boolean confirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }


    public static void pressEnterToContinue() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }
}