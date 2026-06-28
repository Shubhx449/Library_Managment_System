package controller;

import model.*;
import service.*;
import util.InputUtil;

import java.util.stream.Collectors;

public class StudentController {

    private final BookService        bookService        = new BookService();
    private final IssueService       issueService       = new IssueService();
    private final ReservationService reservationService = new ReservationService();
    private final FineService        fineService        = new FineService();
    private final StudentService     studentService     = new StudentService();
    private final DashboardService   dashboardService   = new DashboardService();
    private final AuthenticationService authService     = new AuthenticationService();

    public void showDashboard(int studentId, String name) {
        dashboardService.showStudentDashboard(studentId, name);
    }

    // ── BOOKS ─────────────────────────────────────────────────
    public void searchBook() {
        System.out.println("  1. By Title   2. By Author");
        int choice = InputUtil.readIntInRange("  Choice: ", 1, 2);
        try {
            switch (choice) {
                case 1 -> bookService.printBooks(bookService.searchByTitle(InputUtil.readString("  Keyword: ")));
                case 2 -> bookService.printBooks(bookService.searchByAuthor(InputUtil.readString("  Keyword: ")));
            }
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewAvailableBooks() { bookService.printBooks(bookService.getAvailableBooks()); }

    public void viewBookDetails() {
        String isbn = InputUtil.readString("  Enter ISBN: ");
        try { bookService.printBookDetails(bookService.getBookByIsbn(isbn)); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    // ── BORROWED BOOKS ────────────────────────────────────────
    public void viewMyBorrowedBooks(int studentId) {
        issueService.printIssues(
                issueService.getIssuesByStudent(studentId).stream()
                        .filter(i -> "ISSUED".equals(i.getStatus()) || "OVERDUE".equals(i.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    public void viewBorrowHistory(int studentId) {
        issueService.printIssues(issueService.getIssuesByStudent(studentId));
    }

    // ── RESERVATION ───────────────────────────────────────────
    public void reserveBook(int studentId) {
        String isbn = InputUtil.readString("  Book ISBN: ");
        try {
            Book book = bookService.getBookByIsbn(isbn);
            reservationService.reserveBook(studentId, book.getId());
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewMyReservations(int studentId) {
        reservationService.printReservations(reservationService.getStudentReservations(studentId));
    }

    public void cancelMyReservation(int studentId) {
        int rId = InputUtil.readInt("  Reservation ID to cancel: ");
        try { reservationService.cancelReservation(rId, studentId); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    // ── FINE ──────────────────────────────────────────────────
    public void viewMyFines(int studentId) {
        fineService.printFines(fineService.getStudentFines(studentId));
    }

    // ── PROFILE ───────────────────────────────────────────────
    public void viewProfile(int studentId) {
        try { studentService.printStudentDetails(studentService.getStudentById(studentId)); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void updateProfile(int studentId) {
        try {
            Student s = studentService.getStudentById(studentId);
            String phone = InputUtil.readOptionalString("  New Phone [" + s.getPhone() + "]: ");
            if (!phone.isBlank()) s.setPhone(phone);
            String dept = InputUtil.readOptionalString("  New Dept [" + s.getDepartment() + "]: ");
            if (!dept.isBlank()) s.setDepartment(dept);
            studentService.updateStudent(s);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void changePassword(int userId) {
        String oldPw  = InputUtil.readPassword("  Current Password : ");
        String newPw  = InputUtil.readPassword("  New Password     : ");
        String confPw = InputUtil.readPassword("  Confirm Password : ");
        try { authService.changePassword(userId, oldPw, newPw, confPw); System.out.println("  [✓] Password changed."); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }
}
