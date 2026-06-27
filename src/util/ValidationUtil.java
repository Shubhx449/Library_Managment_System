package util;

public class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isEmpty() || phone.matches("^[6-9]\\d{9}$");
    }

    public static boolean isValidIsbn(String isbn) {
        return isbn != null && (isbn.matches("\\d{10}") || isbn.matches("\\d{13}") || isbn.matches("[\\d-]{10,17}"));
    }

    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isPositive(int n) {
        return n > 0;
    }
}