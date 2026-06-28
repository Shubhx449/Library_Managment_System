package menu;

import controller.StudentController;
import model.Student;
import model.User;
import service.StudentService;
import util.InputUtil;

public class StudentMenu {

    private final User              user;
    private final StudentController controller   = new StudentController();
    private final StudentService    studentService = new StudentService();
    private int studentId = -1;

    public StudentMenu(User user) {
        this.user = user;
        try {
            Student student = studentService.getStudentByUserId(user.getId());
            this.studentId = student.getId();
        } catch (Exception e) {
            System.out.println("  [!] Warning: Could not resolve student profile.");
        }
    }

    public void show() {
        while (true) {
            printMenu();
            int choice = InputUtil.readIntInRange("  Enter Choice: ", 1, 15);
            System.out.println();
            try {
                switch (choice) {
                    case 1  -> controller.showDashboard(studentId, user.getName());
                    case 2  -> controller.searchBook();
                    case 3  -> controller.viewAvailableBooks();
                    case 4  -> controller.viewBookDetails();
                    case 5  -> controller.viewMyBorrowedBooks(studentId);
                    case 6  -> controller.viewBorrowHistory(studentId);
                    case 7  -> controller.reserveBook(studentId);
                    case 8  -> controller.viewMyReservations(studentId);
                    case 9  -> controller.cancelMyReservation(studentId);
                    case 10 -> controller.viewMyFines(studentId);
                    case 11 -> System.out.println("  [Payment History — fine records shown above]");
                    case 12 -> controller.viewProfile(studentId);
                    case 13 -> controller.updateProfile(studentId);
                    case 14 -> controller.changePassword(user.getId());
                    case 15 -> { System.out.println("  Logging out..."); return; }
                }
            } catch (Exception e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
            InputUtil.pressEnterToContinue();
        }
    }

    private void printMenu() {
        System.out.println("\n  ══════════════════════════════");
        System.out.println("         STUDENT MENU          ");
        System.out.println("  ══════════════════════════════");
        System.out.println("  1. Dashboard");
        System.out.println("\n  ── Books ────────────────────");
        System.out.println("  2. Search Book       3. View Available Books");
        System.out.println("  4. View Book Details");
        System.out.println("\n  ── Borrowed Books ───────────");
        System.out.println("  5. My Borrowed Books  6. Borrow History");
        System.out.println("\n  ── Reservation ──────────────");
        System.out.println("  7. Reserve Book       8. My Reservations");
        System.out.println("  9. Cancel Reservation");
        System.out.println("\n  ── Fine ─────────────────────");
        System.out.println(" 10. View Fine Details  11. Payment History");
        System.out.println("\n  ── Profile ──────────────────");
        System.out.println(" 12. View Profile      13. Update Profile");
        System.out.println(" 14. Change Password");
        System.out.println("\n  ── Account ──────────────────");
        System.out.println(" 15. Logout");
        System.out.println("  ══════════════════════════════");
    }
}
