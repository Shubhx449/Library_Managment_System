package controller;

import model.*;
import service.*;
import util.InputUtil;

public class LibrarianController {

    private final BookService        bookService        = new BookService();
    private final StudentService     studentService     = new StudentService();
    private final IssueService       issueService       = new IssueService();
    private final ReservationService reservationService = new ReservationService();
    private final FineService        fineService        = new FineService();
    private final DashboardService   dashboardService   = new DashboardService();
    private final AuthenticationService authService     = new AuthenticationService();

    public void showDashboard() { dashboardService.showLibrarianDashboard(); }


    public void viewBooks() {
        bookService.printBooks(bookService.getAllBooks());
    }

    public void searchBook() {
        System.out.println("  1. By Title   2. By Author   3. By ISBN");
        int choice = InputUtil.readIntInRange("  Choice: ", 1, 3);
        try {
            switch (choice) {
                case 1 -> bookService.printBooks(bookService.searchByTitle(InputUtil.readString("  Keyword: ")));
                case 2 -> bookService.printBooks(bookService.searchByAuthor(InputUtil.readString("  Keyword: ")));
                case 3 -> bookService.printBookDetails(bookService.getBookByIsbn(InputUtil.readString("  ISBN: ")));
            }
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void registerStudent() {
        System.out.println("\n  ── Register Student ──");
        Student student = new Student();
        student.setName(InputUtil.readString("  Name        : "));
        student.setEmail(InputUtil.readString("  Email       : "));
        student.setStudentId(InputUtil.readString("  Student ID  : "));
        student.setPhone(InputUtil.readOptionalString("  Phone       : "));
        student.setDepartment(InputUtil.readString("  Department  : "));
        student.setSemester(InputUtil.readIntInRange("  Semester    : ", 1, 8));
        String password = InputUtil.readPassword("  Password    : ");
        try { studentService.registerStudent(student, password); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewStudents() { studentService.printStudents(studentService.getAllStudents()); }

    public void searchStudent() {
        String keyword = InputUtil.readString("  Name keyword: ");
        studentService.printStudents(studentService.searchByName(keyword));
    }

    public void updateStudent() {
        int id = InputUtil.readInt("  Student DB ID: ");
        try {
            Student s = studentService.getStudentById(id);
            studentService.printStudentDetails(s);
            String phone = InputUtil.readOptionalString("  New Phone [" + s.getPhone() + "]: ");
            if (!phone.isBlank()) s.setPhone(phone);
            String dept = InputUtil.readOptionalString("  New Dept [" + s.getDepartment() + "]: ");
            if (!dept.isBlank()) s.setDepartment(dept);
            String sem = InputUtil.readOptionalString("  New Semester [" + s.getSemester() + "]: ");
            if (!sem.isBlank()) s.setSemester(Integer.parseInt(sem));
            studentService.updateStudent(s);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void issueBook(int librarianId) {
        System.out.println("\n  ── Issue Book ──");
        String studentIdStr = InputUtil.readString("  Student ID  : ");
        String isbn         = InputUtil.readString("  Book ISBN   : ");
        try {
            Student student = studentService.getStudentByStudentId(studentIdStr);
            Book    book    = bookService.getBookByIsbn(isbn);
            issueService.issueBook(student.getId(), book.getId(), librarianId);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void returnBook(int librarianId) {
        System.out.println("\n  ── Return Book ──");
        int issueId = InputUtil.readInt("  Issue ID: ");
        try { issueService.returnBook(issueId, librarianId); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void renewBook() {
        int issueId = InputUtil.readInt("  Issue ID to renew: ");
        try { issueService.renewBook(issueId); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void viewReservations() { reservationService.printReservations(reservationService.getPendingReservations()); }

    public void cancelReservation() {
        int rId = InputUtil.readInt("  Reservation ID: ");
        int sId = InputUtil.readInt("  Student DB ID : ");
        try { reservationService.cancelReservation(rId, sId); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void viewPendingFines() { fineService.printFines(fineService.getPendingFines()); }

    public void collectFine(int librarianId) {
        int fineId = InputUtil.readInt("  Fine ID: ");
        try { fineService.collectFine(fineId, librarianId); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void issueHistory()       { issueService.printIssues(issueService.getAllActiveIssues()); }
    public void todayTransactions() {
        System.out.println("\n  ── Issued Today ──");
        issueService.printIssues(issueService.getIssuedToday());
        System.out.println("\n  ── Returned Today ──");
        issueService.printIssues(issueService.getReturnedToday());
    }


    public void changePassword(int userId) {
        String oldPw  = InputUtil.readPassword("  Current Password : ");
        String newPw  = InputUtil.readPassword("  New Password     : ");
        String confPw = InputUtil.readPassword("  Confirm Password : ");
        try { authService.changePassword(userId, oldPw, newPw, confPw); System.out.println("  [✓] Password changed."); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }
}
