package controller;

import model.*;
import service.*;
import util.InputUtil;

public class AdminController {

    private final BookService        bookService        = new BookService();
    private final StudentService     studentService     = new StudentService();
    private final LibrarianService   librarianService   = new LibrarianService();
    private final CategoryService    categoryService    = new CategoryService();
    private final IssueService       issueService       = new IssueService();
    private final FineService        fineService        = new FineService();
    private final ReportService      reportService      = new ReportService();
    private final DashboardService   dashboardService   = new DashboardService();
    private final AuthenticationService authService     = new AuthenticationService();


    public void showDashboard() {
        dashboardService.showAdminDashboard();
    }


    public void addBook() {
        System.out.println("\n  ── Add New Book ──");
        categoryService.printAllCategories();

        Book book = new Book();
        book.setTitle(InputUtil.readString("  Title         : "));
        book.setAuthor(InputUtil.readString("  Author        : "));
        book.setIsbn(InputUtil.readString("  ISBN          : "));
        book.setCategoryId(InputUtil.readInt("  Category ID   : "));
        book.setPublisher(InputUtil.readOptionalString("  Publisher     : "));
        book.setPublishYear(InputUtil.readInt("  Publish Year  : "));
        book.setTotalCopies(InputUtil.readInt("  Total Copies  : "));
        book.setShelfLocation(InputUtil.readOptionalString("  Shelf Location: "));

        try { bookService.addBook(book); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewAllBooks() {
        System.out.println("\n  ── All Books ──");
        bookService.printBooks(bookService.getAllBooks());
    }

    public void searchBook() {
        System.out.println("\n  ── Search Book ──");
        System.out.println("  1. By Title   2. By Author   3. By ISBN");
        int choice = InputUtil.readIntInRange("  Choice: ", 1, 3);
        try {
            switch (choice) {
                case 1 -> bookService.printBooks(bookService.searchByTitle(InputUtil.readString("  Title keyword: ")));
                case 2 -> bookService.printBooks(bookService.searchByAuthor(InputUtil.readString("  Author keyword: ")));
                case 3 -> bookService.printBookDetails(bookService.getBookByIsbn(InputUtil.readString("  ISBN: ")));
            }
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void updateBook() {
        System.out.println("\n  ── Update Book ──");
        int id = InputUtil.readInt("  Enter Book ID: ");
        try {
            Book book = bookService.getBookById(id);
            bookService.printBookDetails(book);
            System.out.println("  (Press Enter to keep current value)");
            String title = InputUtil.readOptionalString("  New Title [" + book.getTitle() + "]: ");
            if (!title.isBlank()) book.setTitle(title);
            String author = InputUtil.readOptionalString("  New Author [" + book.getAuthor() + "]: ");
            if (!author.isBlank()) book.setAuthor(author);
            String copies = InputUtil.readOptionalString("  Total Copies [" + book.getTotalCopies() + "]: ");
            if (!copies.isBlank()) book.setTotalCopies(Integer.parseInt(copies));
            bookService.updateBook(book);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void deleteBook() {
        System.out.println("\n  ── Delete Book ──");
        int id = InputUtil.readInt("  Enter Book ID: ");
        if (InputUtil.confirm("  Confirm delete book ID " + id + "?"))
            try { bookService.deleteBook(id); }
            catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void addCategory() {
        System.out.println("\n  ── Add Category ──");
        String name = InputUtil.readString("  Category Name: ");
        String desc = InputUtil.readOptionalString("  Description  : ");
        try { categoryService.addCategory(name, desc); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewCategories() {
        System.out.println("\n  ── All Categories ──");
        categoryService.printAllCategories();
    }

    public void updateCategory() {
        System.out.println("\n  ── Update Category ──");
        int id = InputUtil.readInt("  Category ID: ");
        try {
            String name = InputUtil.readString("  New Name: ");
            String desc = InputUtil.readOptionalString("  New Description: ");
            categoryService.updateCategory(id, name, desc);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void deleteCategory() {
        int id = InputUtil.readInt("  Category ID to delete: ");
        if (InputUtil.confirm("  Confirm delete?"))
            try { categoryService.deleteCategory(id); }
            catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void addLibrarian() {
        System.out.println("\n  ── Add Librarian ──");
        Librarian lib = new Librarian();
        lib.setName(InputUtil.readString("  Name        : "));
        lib.setEmail(InputUtil.readString("  Email       : "));
        lib.setEmployeeId(InputUtil.readString("  Employee ID : "));
        lib.setPhone(InputUtil.readOptionalString("  Phone       : "));
        lib.setAddress(InputUtil.readOptionalString("  Address     : "));
        String password = InputUtil.readPassword("  Password    : ");
        try { librarianService.addLibrarian(lib, password); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void viewLibrarians() {
        System.out.println("\n  ── All Librarians ──");
        librarianService.printLibrarians(librarianService.getAllLibrarians());
    }

    public void searchLibrarian() {
        String keyword = InputUtil.readString("  Name keyword: ");
        librarianService.printLibrarians(librarianService.searchByName(keyword));
    }

    public void updateLibrarian() {
        int id = InputUtil.readInt("  Librarian ID: ");
        try {
            Librarian lib = librarianService.getLibrarianById(id);
            String phone = InputUtil.readOptionalString("  New Phone [" + lib.getPhone() + "]: ");
            if (!phone.isBlank()) lib.setPhone(phone);
            String address = InputUtil.readOptionalString("  New Address: ");
            if (!address.isBlank()) lib.setAddress(address);
            librarianService.updateLibrarian(lib);
        } catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void deleteLibrarian() {
        int id = InputUtil.readInt("  Librarian ID to deactivate: ");
        if (InputUtil.confirm("  Confirm deactivation?"))
            try { librarianService.deleteLibrarian(id); }
            catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }

    public void updateStudentFromAdmin() {
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

    public void viewStudents() {
        System.out.println("\n  ── All Students ──");
        studentService.printStudents(studentService.getAllStudents());
    }

    public void searchStudent() {
        String keyword = InputUtil.readString("  Name keyword: ");
        studentService.printStudents(studentService.searchByName(keyword));
    }

    public void deleteStudent() {
        int id = InputUtil.readInt("  Student DB ID to deactivate: ");
        if (InputUtil.confirm("  Confirm deactivation?"))
            try { studentService.deleteStudent(id); }
            catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }


    public void viewIssuedBooks()   { issueService.printIssues(issueService.getAllActiveIssues()); }
    public void viewOverdueBooks()  { issueService.printIssues(issueService.getAllOverdueIssues()); }
    public void viewReturnedBooks() { issueService.printIssues(issueService.getAllReturnedIssues()); }


    public void mostIssuedBooks()     { reportService.showMostIssuedBooks(); }
    public void fineCollectionReport() { reportService.showFineCollectionReport(); }
    public void activeStudentsReport() { reportService.showActiveStudentsReport(); }


    public void changePassword(int userId) {
        String oldPw  = InputUtil.readPassword("  Current Password : ");
        String newPw  = InputUtil.readPassword("  New Password     : ");
        String confPw = InputUtil.readPassword("  Confirm Password : ");
        try { authService.changePassword(userId, oldPw, newPw, confPw); System.out.println("  [✓] Password changed."); }
        catch (Exception e) { System.out.println("  [✗] " + e.getMessage()); }
    }
}
