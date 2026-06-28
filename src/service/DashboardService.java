package service;

import dao.ReportDAO;
import dao.impl.ReportDAOImpl;

import java.math.BigDecimal;

public class DashboardService {

    private final ReportDAO  reportDAO  = new ReportDAOImpl();
    private final FineService fineService = new FineService();

    public void showAdminDashboard() {
        int totalBooks      = reportDAO.totalBooks();
        int availableBooks  = reportDAO.totalAvailableBooks();
        int issuedBooks     = reportDAO.totalIssuedBooks();
        int totalStudents   = reportDAO.totalStudents();
        int totalLibrarians = reportDAO.totalLibrarians();
        int overdueBooks    = reportDAO.totalOverdueBooks();
        int todayTx         = reportDAO.todayTransactions();
        BigDecimal pendFine = fineService.getTotalPendingFines();

        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║               ADMIN DASHBOARD                    ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  %-25s : %-20d║%n", "Total Books",        totalBooks);
        System.out.printf("  ║  %-25s : %-20d║%n", "Available Books",    availableBooks);
        System.out.printf("  ║  %-25s : %-20d║%n", "Issued Books",       issuedBooks);
        System.out.printf("  ║  %-25s : %-20d║%n", "Total Students",     totalStudents);
        System.out.printf("  ║  %-25s : %-20d║%n", "Total Librarians",   totalLibrarians);
        System.out.printf("  ║  %-25s : %-20d║%n", "Overdue Books",      overdueBooks);
        System.out.printf("  ║  %-25s : %-20d║%n", "Today's Transactions", todayTx);
        System.out.printf("  ║  %-25s : ₹%-19.2f║%n", "Pending Fine",   pendFine);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    public void showLibrarianDashboard() {
        int availableBooks = reportDAO.totalAvailableBooks();
        int issuedToday    = reportDAO.todayTransactions();
        int overdueBooks   = reportDAO.totalOverdueBooks();
        BigDecimal pendFine = fineService.getTotalPendingFines();

        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║             LIBRARIAN DASHBOARD                  ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  %-25s : %-20d║%n", "Books Available",    availableBooks);
        System.out.printf("  ║  %-25s : %-20d║%n", "Today's Transactions", issuedToday);
        System.out.printf("  ║  %-25s : %-20d║%n", "Overdue Books",      overdueBooks);
        System.out.printf("  ║  %-25s : ₹%-19.2f║%n", "Pending Fines",  pendFine);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    public void showStudentDashboard(int studentId, String studentName) {
        FineService fs = new FineService();
        IssueService is = new IssueService();
        ReservationService rs = new ReservationService();

        int borrowed    = is.getIssuesByStudent(studentId).stream()
                .filter(i -> "ISSUED".equals(i.getStatus()) || "OVERDUE".equals(i.getStatus()))
                .mapToInt(i -> 1).sum();
        int reserved    = rs.getStudentReservations(studentId).stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .mapToInt(r -> 1).sum();
        long pendFine   = fs.getStudentFines(studentId).stream()
                .filter(f -> "PENDING".equals(f.getStatus()) && f.getAmount() != null)
                .mapToLong(f -> f.getAmount().longValue()).sum();

        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.printf("  ║  Welcome, %-38s║%n", studentName + "!");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  %-25s : %-20d║%n", "Books Borrowed",     borrowed);
        System.out.printf("  ║  %-25s : %-20d║%n", "Books Reserved",     reserved);
        System.out.printf("  ║  %-25s : ₹%-19d║%n", "Pending Fine",     pendFine);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }
}
