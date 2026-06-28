package service;

import dao.BookDAO;
import dao.FineDAO;
import dao.IssueDAO;
import dao.StudentDAO;
import dao.impl.BookDAOImpl;
import dao.impl.FineDAOImpl;
import dao.impl.IssueDAOImpl;
import dao.impl.StudentDAOImpl;
import exception.BookNotFoundException;
import exception.StudentNotFoundException;
import model.Book;
import model.Fine;
import model.Issue;
import model.Student;
import util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class IssueService {

    private final IssueDAO   issueDAO   = new IssueDAOImpl();
    private final BookDAO    bookDAO    = new BookDAOImpl();
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final FineDAO    fineDAO    = new FineDAOImpl();


    public Issue issueBook(int studentId, int bookId, int librarianId) {

        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));


        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found."));


        if (!book.isAvailable())
            throw new IllegalStateException("No copies of '" + book.getTitle() + "' are available.");


        int currentCount = studentDAO.countCurrentlyIssuedBooks(studentId);
        if (currentCount >= student.getMaxBooks())
            throw new IllegalStateException("Student has reached the borrow limit (" + student.getMaxBooks() + " books).");


        if (issueDAO.hasActiveIssueForBook(studentId, bookId))
            throw new IllegalStateException("Student already has this book issued.");


        List<Fine> studentFines = fineDAO.findByStudent(studentId);
        boolean hasPendingFine = studentFines.stream().anyMatch(f -> "PENDING".equals(f.getStatus()));
        if (hasPendingFine)
            throw new IllegalStateException("Student has unpaid fines. Please clear fines before issuing.");


        Issue issue = new Issue();
        issue.setStudentId(studentId);
        issue.setBookId(bookId);
        issue.setLibrarianId(librarianId);
        issue.setIssuedDate(DateUtil.today());
        issue.setDueDate(DateUtil.dueDate());
        issue.setStatus("ISSUED");

        int issueId = issueDAO.save(issue);
        if (issueId == -1)
            throw new RuntimeException("Failed to issue book. Please try again.");

        issue.setId(issueId);


        bookDAO.decrementAvailable(bookId);

        System.out.println("  [✓] Book issued successfully!");
        System.out.println("      Issue ID  : " + issueId);
        System.out.println("      Book      : " + book.getTitle());
        System.out.println("      Student   : " + student.getName());
        System.out.println("      Due Date  : " + DateUtil.format(issue.getDueDate()));

        return issue;
    }


    public void returnBook(int issueId, int librarianId) {
        Issue issue = issueDAO.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found with ID: " + issueId));

        if ("RETURNED".equals(issue.getStatus()))
            throw new IllegalStateException("This book has already been returned.");

        LocalDate returnDate = DateUtil.today();
        issueDAO.markReturned(issueId, returnDate);
        bookDAO.incrementAvailable(issue.getBookId());


        long overdueDays = DateUtil.overdueDays(issue.getDueDate());
        if (overdueDays > 0) {
            double fineAmount = DateUtil.calculateFine(issue.getDueDate());
            Fine fine = new Fine();
            fine.setIssueId(issueId);
            fine.setStudentId(issue.getStudentId());
            fine.setAmount(BigDecimal.valueOf(fineAmount));
            fineDAO.save(fine);
            System.out.printf("  [!] Book returned %d day(s) late. Fine: ₹%.2f%n", overdueDays, fineAmount);
        } else {
            System.out.println("  [✓] Book returned successfully. No fine.");
        }

        System.out.println("      Book    : " + issue.getBookTitle());
        System.out.println("      Student : " + issue.getStudentName());
        System.out.println("      Returned: " + DateUtil.format(returnDate));
    }

    public void renewBook(int issueId) {
        Issue issue = issueDAO.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found."));

        if ("RETURNED".equals(issue.getStatus()))
            throw new IllegalStateException("Cannot renew a returned book.");

        if (issue.isOverdue())
            throw new IllegalStateException("Cannot renew an overdue book. Please return and pay fine.");

        LocalDate newDueDate = issue.getDueDate().plusDays(DateUtil.RENEWAL_DAYS);
        if (!issueDAO.updateDueDate(issueId, newDueDate))
            throw new RuntimeException("Failed to renew book.");

        System.out.println("  [✓] Book renewed successfully.");
        System.out.println("      New Due Date: " + DateUtil.format(newDueDate));
    }

    public List<Issue> getAllActiveIssues()     { return issueDAO.findActive(); }
    public List<Issue> getAllOverdueIssues()    { return issueDAO.findOverdue(); }
    public List<Issue> getAllReturnedIssues()   { return issueDAO.findReturned(); }
    public List<Issue> getIssuedToday()         { return issueDAO.findIssuedToday(); }
    public List<Issue> getReturnedToday()       { return issueDAO.findReturnedToday(); }
    public List<Issue> getIssuesByStudent(int studentId) { return issueDAO.findByStudent(studentId); }

    public void printIssues(List<Issue> issues) {
        if (issues.isEmpty()) { System.out.println("  No records found."); return; }
        System.out.println("\n  +-----+------------+---------------------------+------------+------------+----------+");
        System.out.printf("  | %-4s| %-10s | %-25s | %-10s | %-10s | %-8s |%n",
                "ID", "Student ID", "Book Title", "Issued", "Due Date", "Status");
        System.out.println("  +-----+------------+---------------------------+------------+------------+----------+");
        for (Issue i : issues) {
            System.out.printf("  | %-4d| %-10s | %-25s | %-10s | %-10s | %-8s |%n",
                    i.getId(),
                    i.getStudentIdStr(),
                    truncate(i.getBookTitle(), 25),
                    DateUtil.format(i.getIssuedDate()),
                    DateUtil.format(i.getDueDate()),
                    i.getStatus());
        }
        System.out.println("  +-----+------------+---------------------------+------------+------------+----------+");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
