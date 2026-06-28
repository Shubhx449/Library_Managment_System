package dao.impl;

import config.DBConnection;
import dao.ReportDAO;

import java.sql.*;
import java.util.LinkedHashMap;

public class ReportDAOImpl implements ReportDAO {

    @Override
    public LinkedHashMap<String, Integer> mostIssuedBooks(int limit) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT b.title, COUNT(ib.id) AS issue_count " +
                "FROM issued_books ib JOIN books b ON ib.book_id = b.id " +
                "GROUP BY b.id ORDER BY issue_count DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.put(rs.getString("title"), rs.getInt("issue_count"));
        } catch (SQLException e) {
            System.err.println("[ReportDAO] mostIssuedBooks error: " + e.getMessage());
        }
        return result;
    }

    @Override
    public LinkedHashMap<String, Integer> activeStudents(int limit) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT u.name, s.student_id, COUNT(ib.id) AS borrow_count " +
                "FROM issued_books ib " +
                "JOIN students s ON ib.student_id = s.id " +
                "JOIN users u ON s.user_id = u.id " +
                "GROUP BY s.id ORDER BY borrow_count DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                result.put(rs.getString("name") + " (" + rs.getString("student_id") + ")",
                        rs.getInt("borrow_count"));
        } catch (SQLException e) {
            System.err.println("[ReportDAO] activeStudents error: " + e.getMessage());
        }
        return result;
    }

    @Override
    public LinkedHashMap<String, Double> fineCollectionByDay(int days) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT DATE(collected_at) AS day, SUM(paid_amount) AS total " +
                "FROM fines WHERE status = 'PAID' AND collected_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY day ORDER BY day DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.put(rs.getString("day"), rs.getDouble("total"));
        } catch (SQLException e) {
            System.err.println("[ReportDAO] fineCollectionByDay error: " + e.getMessage());
        }
        return result;
    }

    @Override
    public int totalBooks() { return countFrom("SELECT COUNT(*) FROM books"); }

    @Override
    public int totalAvailableBooks() { return countFrom("SELECT COUNT(*) FROM books WHERE available_copies > 0"); }

    @Override
    public int totalIssuedBooks() { return countFrom("SELECT COUNT(*) FROM issued_books WHERE status IN ('ISSUED','OVERDUE')"); }

    @Override
    public int totalStudents() { return countFrom("SELECT COUNT(*) FROM users WHERE role='STUDENT' AND is_active=TRUE"); }

    @Override
    public int totalLibrarians() { return countFrom("SELECT COUNT(*) FROM users WHERE role='LIBRARIAN' AND is_active=TRUE"); }

    @Override
    public int totalOverdueBooks() { return countFrom("SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL AND due_date < CURDATE()"); }

    @Override
    public int todayTransactions() { return countFrom("SELECT COUNT(*) FROM issued_books WHERE DATE(issued_date)=CURDATE() OR DATE(return_date)=CURDATE()"); }

    private int countFrom(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ReportDAO] count error: " + e.getMessage());
        }
        return 0;
    }
}
