package menu;

import controller.LibrarianController;
import model.Librarian;
import model.User;
import service.LibrarianService;
import util.InputUtil;

public class LibrarianMenu {

    private final User                user;
    private final LibrarianController controller  = new LibrarianController();
    private final LibrarianService    libService  = new LibrarianService();
    private int librarianId = -1;

    public LibrarianMenu(User user) {
        this.user = user;
        // Resolve the librarian profile ID for operations that need it
        try {
            Librarian lib = libService.getLibrarianByUserId(user.getId());
            this.librarianId = lib.getId();
        } catch (Exception e) {
            System.out.println("  [!] Warning: Could not resolve librarian profile.");
        }
    }

    public void show() {
        while (true) {
            printMenu();
            int choice = InputUtil.readIntInRange("  Enter Choice: ", 1, 19);
            System.out.println();
            try {
                switch (choice) {
                    case 1  -> controller.showDashboard();
                    case 2  -> controller.viewBooks();
                    case 3  -> controller.searchBook();
                    case 4  -> controller.registerStudent();
                    case 5  -> controller.viewStudents();
                    case 6  -> controller.searchStudent();
                    case 7  -> controller.updateStudent();
                    case 8  -> controller.issueBook(librarianId);
                    case 9  -> controller.returnBook(librarianId);
                    case 10 -> controller.renewBook();
                    case 11 -> System.out.println("  [Reserve Book — student self-service]");
                    case 12 -> controller.viewReservations();
                    case 13 -> controller.cancelReservation();
                    case 14 -> controller.viewPendingFines();
                    case 15 -> controller.collectFine(librarianId);
                    case 16 -> controller.issueHistory();
                    case 17 -> controller.todayTransactions();
                    case 18 -> controller.changePassword(user.getId());
                    case 19 -> { System.out.println("  Logging out..."); return; }
                }
            } catch (Exception e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
            InputUtil.pressEnterToContinue();
        }
    }

    private void printMenu() {
        System.out.println("\n  ══════════════════════════════");
        System.out.println("        LIBRARIAN MENU         ");
        System.out.println("  ══════════════════════════════");
        System.out.println("  1. Dashboard");
        System.out.println("\n  ── Book Management ──────────");
        System.out.println("  2. View Books        3. Search Book");
        System.out.println("\n  ── Student Management ───────");
        System.out.println("  4. Register Student  5. View Students");
        System.out.println("  6. Search Student    7. Update Student");
        System.out.println("\n  ── Book Operations ──────────");
        System.out.println("  8. Issue Book        9. Return Book");
        System.out.println(" 10. Renew Book");
        System.out.println("\n  ── Reservation ──────────────");
        System.out.println(" 11. Reserve Book     12. View Reservations");
        System.out.println(" 13. Cancel Reservation");
        System.out.println("\n  ── Fine ─────────────────────");
        System.out.println(" 14. View Pending Fines");
        System.out.println(" 15. Collect Fine");
        System.out.println("\n  ── Reports ──────────────────");
        System.out.println(" 16. Issue History    17. Today's Transactions");
        System.out.println("\n  ── Account ──────────────────");
        System.out.println(" 18. Change Password  19. Logout");
        System.out.println("  ══════════════════════════════");
    }
}
