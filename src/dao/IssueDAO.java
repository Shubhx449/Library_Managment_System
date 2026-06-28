package dao;

import model.Issue;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IssueDAO {
    int save(Issue issue);                              // returns generated id
    Optional<Issue> findById(int id);
    List<Issue> findByStudent(int studentId);
    List<Issue> findActive();                           // status = ISSUED/OVERDUE
    List<Issue> findOverdue();
    List<Issue> findReturned();
    List<Issue> findIssuedToday();
    List<Issue> findReturnedToday();
    boolean markReturned(int issueId, LocalDate returnDate);
    boolean updateDueDate(int issueId, LocalDate newDueDate);
    boolean updateStatus(int issueId, String status);
    boolean hasActiveIssueForBook(int studentId, int bookId);
}
