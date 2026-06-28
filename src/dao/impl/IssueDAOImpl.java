package dao.impl;

import config.DBConnection;
import dao.IssueDAO;
import model.Issue;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueDAOImpl implements IssueDAO {


    private static final String SELECT_FULL =
            "SELECT ib.*, " +
                    "  u.name AS student_name, s.student_id AS student_id_str, " +
                    "  b.title AS book_title, b.isbn AS book_isbn, " +
                    "  ul.name AS librarian_name " +
                    "FROM issued_books ib " +
                    "JOIN students s ON ib.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "JOIN books b ON ib.book_id = b.id " +
                    "JOIN librarians l ON ib.librarian_id = l.id " +
                    "JOIN users ul ON l.user_id = ul.id";

    @Override
    public int save(Issue issue) {
        String sql = "INSERT INTO issued_books (student_id, book_id, librarian_id, " +
                "issued_date, due_date, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, issue.getStudentId());
            ps.setInt(2, issue.getBookId());
            ps.setInt(3, issue.getLibrarianId());
            ps.setDate(4, Date.valueOf(issue.getIssuedDate()));
            ps.setDate(5, Date.valueOf(issue.getDueDate()));
            ps.setString(6, "ISSUED");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[IssueDAO] save error: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public Optional<Issue> findById(int id) {
        String sql = SELECT_FULL + " WHERE ib.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Issue> findByStudent(int studentId) {
        return findWhere("ib.student_id = ?", studentId);
    }

    @Override
    public List<Issue> findActive() {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE ib.status IN ('ISSUED','OVERDUE') ORDER BY ib.due_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findActive error: " + e.getMessage());
        }
        return issues;
    }

    @Override
    public List<Issue> findOverdue() {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE ib.return_date IS NULL AND ib.due_date < CURDATE() ORDER BY ib.due_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findOverdue error: " + e.getMessage());
        }
        return issues;
    }

    @Override
    public List<Issue> findReturned() {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE ib.status = 'RETURNED' ORDER BY ib.return_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findReturned error: " + e.getMessage());
        }
        return issues;
    }

    @Override
    public List<Issue> findIssuedToday() {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE DATE(ib.issued_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findIssuedToday error: " + e.getMessage());
        }
        return issues;
    }

    @Override
    public List<Issue> findReturnedToday() {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE DATE(ib.return_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findReturnedToday error: " + e.getMessage());
        }
        return issues;
    }

    @Override
    public boolean markReturned(int issueId, LocalDate returnDate) {
        String sql = "UPDATE issued_books SET return_date = ?, status = 'RETURNED' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] markReturned error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateDueDate(int issueId, LocalDate newDueDate) {
        String sql = "UPDATE issued_books SET due_date = ? WHERE id = ? AND status = 'ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(newDueDate));
            ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] updateDueDate error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateStatus(int issueId, String status) {
        String sql = "UPDATE issued_books SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean hasActiveIssueForBook(int studentId, int bookId) {
        String sql = "SELECT COUNT(*) FROM issued_books " +
                "WHERE student_id = ? AND book_id = ? AND status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] hasActiveIssueForBook error: " + e.getMessage());
        }
        return false;
    }

    private List<Issue> findWhere(String condition, int param) {
        List<Issue> issues = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE " + condition + " ORDER BY ib.issued_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) issues.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] findWhere error: " + e.getMessage());
        }
        return issues;
    }

    private Issue mapRow(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setId(rs.getInt("id"));
        i.setStudentId(rs.getInt("student_id"));
        i.setBookId(rs.getInt("book_id"));
        i.setLibrarianId(rs.getInt("librarian_id"));
        i.setIssuedDate(rs.getDate("issued_date").toLocalDate());
        i.setDueDate(rs.getDate("due_date").toLocalDate());
        Date ret = rs.getDate("return_date");
        if (ret != null) i.setReturnDate(ret.toLocalDate());
        i.setStatus(rs.getString("status"));
        i.setStudentName(rs.getString("student_name"));
        i.setStudentIdStr(rs.getString("student_id_str"));
        i.setBookTitle(rs.getString("book_title"));
        i.setBookIsbn(rs.getString("book_isbn"));
        i.setLibrarianName(rs.getString("librarian_name"));
        return i;
    }
}
