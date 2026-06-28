package menu;

import controller.AdminController;
import model.User;
import util.InputUtil;

public class AdminMenu {

    private final User            user;
    private final AdminController controller = new AdminController();

    public AdminMenu(User user) { this.user = user; }

    public void show() {
        while (true) {
            printMenu();
            int choice = InputUtil.readIntInRange("  Enter Choice: ", 1, 28);
            System.out.println();
            try {
                switch (choice) {
                    case 1  -> controller.showDashboard();
                    case 2  -> controller.addBook();
                    case 3  -> controller.viewAllBooks();
                    case 4  -> controller.searchBook();
                    case 5  -> controller.updateBook();
                    case 6  -> controller.deleteBook();
                    case 7  -> controller.addCategory();
                    case 8  -> controller.viewCategories();
                    case 9  -> controller.updateCategory();
                    case 10 -> controller.deleteCategory();
                    case 11 -> controller.addLibrarian();
                    case 12 -> controller.viewLibrarians();
                    case 13 -> controller.searchLibrarian();
                    case 14 -> controller.updateLibrarian();
                    case 15 -> controller.deleteLibrarian();
                    case 16 -> controller.viewStudents();
                    case 17 -> controller.searchStudent();
                    case 18 -> controller.updateStudentFromAdmin();
                    case 19 -> controller.deleteStudent();
                    case 20 -> controller.viewIssuedBooks();
                    case 21 -> controller.viewOverdueBooks();
                    case 22 -> controller.viewReturnedBooks();
                    case 23 -> controller.mostIssuedBooks();
                    case 24 -> controller.fineCollectionReport();
                    case 25 -> controller.activeStudentsReport();
                    case 26 -> System.out.println("  [Activity Log not implemented yet]");
                    case 27 -> controller.changePassword(user.getId());
                    case 28 -> { System.out.println("  Logging out..."); return; }
                }
            } catch (Exception e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
            InputUtil.pressEnterToContinue();
        }
    }

    private void printMenu() {
        System.out.println("\n  ══════════════════════════════");
        System.out.println("           ADMIN MENU          ");
        System.out.println("  ══════════════════════════════");
        System.out.println("  1. Dashboard");
        System.out.println("\n  ── Book Management ──────────");
        System.out.println("  2. Add Book          3. View All Books");
        System.out.println("  4. Search Book       5. Update Book");
        System.out.println("  6. Delete Book");
        System.out.println("\n  ── Category Management ──────");
        System.out.println("  7. Add Category      8. View Categories");
        System.out.println("  9. Update Category  10. Delete Category");
        System.out.println("\n  ── Librarian Management ─────");
        System.out.println(" 11. Add Librarian    12. View Librarians");
        System.out.println(" 13. Search Librarian 14. Update Librarian");
        System.out.println(" 15. Delete Librarian");
        System.out.println("\n  ── Student Management ───────");
        System.out.println(" 16. View Students    17. Search Student");
        System.out.println(" 18. Update Student   19. Delete Student");
        System.out.println("\n  ── Issue Management ─────────");
        System.out.println(" 20. View Issued Books");
        System.out.println(" 21. View Overdue Books");
        System.out.println(" 22. View Returned Books");
        System.out.println("\n  ── Reports ──────────────────");
        System.out.println(" 23. Most Issued Books");
        System.out.println(" 24. Fine Collection Report");
        System.out.println(" 25. Active Students Report");
        System.out.println("\n  ── Logs ─────────────────────");
        System.out.println(" 26. View Activity Logs");
        System.out.println("\n  ── Account ──────────────────");
        System.out.println(" 27. Change Password  28. Logout");
        System.out.println("  ══════════════════════════════");
    }
}
