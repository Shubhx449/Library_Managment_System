package service;

import dao.FineDAO;
import dao.impl.FineDAOImpl;
import model.Fine;

import java.math.BigDecimal;
import java.util.List;

public class FineService {

    private final FineDAO fineDAO = new FineDAOImpl();

    public List<Fine> getPendingFines()                     { return fineDAO.findPending(); }
    public List<Fine> getStudentFines(int studentId)        { return fineDAO.findByStudent(studentId); }
    public BigDecimal getTotalCollectedToday()              { return fineDAO.totalCollectedToday(); }
    public BigDecimal getTotalPendingFines()                { return fineDAO.totalPendingFines(); }

    public void collectFine(int fineId, int librarianId) {
        Fine fine = fineDAO.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine record not found."));

        if (!"PENDING".equals(fine.getStatus()))
            throw new IllegalStateException("This fine is already " + fine.getStatus() + ".");

        if (!fineDAO.markPaid(fineId, librarianId, fine.getAmount()))
            throw new RuntimeException("Failed to collect fine.");

        System.out.printf("  [✓] Fine of ₹%.2f collected successfully.%n", fine.getAmount());
    }

    public void waiveFine(int fineId, int librarianId) {
        Fine fine = fineDAO.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine record not found."));

        if (!"PENDING".equals(fine.getStatus()))
            throw new IllegalStateException("Only PENDING fines can be waived.");

        if (!fineDAO.waive(fineId, librarianId))
            throw new RuntimeException("Failed to waive fine.");

        System.out.printf("  [✓] Fine of ₹%.2f waived successfully.%n", fine.getAmount());
    }

    public void printFines(List<Fine> fines) {
        if (fines.isEmpty()) { System.out.println("  No fines found."); return; }
        System.out.println("\n  +-----+------------+----------------------+---------------------------+---------+----------+");
        System.out.printf("  | %-4s| %-10s | %-20s | %-25s | %-7s | %-8s |%n",
                "ID", "Student ID", "Student Name", "Book", "Amount", "Status");
        System.out.println("  +-----+------------+----------------------+---------------------------+---------+----------+");
        for (Fine f : fines) {
            System.out.printf("  | %-4d| %-10s | %-20s | %-25s | ₹%-6.2f | %-8s |%n",
                    f.getId(),
                    f.getStudentIdStr(),
                    truncate(f.getStudentName(), 20),
                    truncate(f.getBookTitle(), 25),
                    f.getAmount(),
                    f.getStatus());
        }
        System.out.println("  +-----+------------+----------------------+---------------------------+---------+----------+");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
