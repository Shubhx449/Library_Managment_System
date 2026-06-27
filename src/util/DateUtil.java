package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static final int LOAN_PERIOD_DAYS   = 14;
    public static final int RENEWAL_DAYS       = 7;
    public static final int RESERVATION_EXPIRY = 3;
    public static final double FINE_PER_DAY    = 2.0;

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private DateUtil() {}

    public static LocalDate today()                  { return LocalDate.now(); }
    public static LocalDate dueDate()                { return today().plusDays(LOAN_PERIOD_DAYS); }
    public static LocalDate reservationExpiry()      { return today().plusDays(RESERVATION_EXPIRY); }


    public static long daysBetween(LocalDate from, LocalDate to) {
        return Math.abs(ChronoUnit.DAYS.between(from, to));
    }


    public static long overdueDays(LocalDate dueDate) {
        LocalDate now = today();
        return now.isAfter(dueDate) ? ChronoUnit.DAYS.between(dueDate, now) : 0;
    }


    public static double calculateFine(LocalDate dueDate) {
        return overdueDays(dueDate) * FINE_PER_DAY;
    }


    public static String format(LocalDate date) {
        return date != null ? date.format(DISPLAY_FMT) : "N/A";
    }
}