package dao;

import model.Fine;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FineDAO {
    boolean save(Fine fine);
    Optional<Fine> findById(int id);
    Optional<Fine> findByIssueId(int issueId);
    List<Fine> findByStudent(int studentId);
    List<Fine> findPending();
    boolean markPaid(int fineId, int librarianId, BigDecimal paidAmount);
    boolean waive(int fineId, int librarianId);
    BigDecimal totalCollectedToday();
    BigDecimal totalPendingFines();
}
