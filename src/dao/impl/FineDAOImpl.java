package dao.impl;

import config.DBConnection;
import dao.FineDAO;
import model.Fine;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineDAOImpl implements FineDAO {

    private static final String SELECT_FULL =
            "SELECT f.*, u.name AS student_name, s.student_id AS student_id_str, b.title AS book_title " +
                    "FROM fines f " +
                    "JOIN students s ON f.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "JOIN issued_books ib ON f.issue_id = ib.id " +
                    "JOIN books b ON ib.book_id = b.id";

    @Override
    public boolean save(Fine fine) {
        String sql = "INSERT INTO fines (issue_id, student_id, amount) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fine.getIssueId());
            ps.setInt(2, fine.getStudentId());
            ps.setBigDecimal(3, fine.getAmount());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) fine.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[FineDAO] save error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Fine> findById(int id) {
        String sql = SELECT_FULL + " WHERE f.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[FineDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Fine> findByIssueId(int issueId) {
        String sql = SELECT_FULL + " WHERE f.issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[FineDAO] findByIssueId error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Fine> findByStudent(int studentId) {
        List<Fine> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE f.student_id = ? ORDER BY f.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[FineDAO] findByStudent error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Fine> findPending() {
        List<Fine> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE f.status = 'PENDING' ORDER BY f.amount DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[FineDAO] findPending error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean markPaid(int fineId, int librarianId, BigDecimal paidAmount) {
        String sql = "UPDATE fines SET status='PAID', paid_amount=?, collected_by=?, collected_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, paidAmount);
            ps.setInt(2, librarianId);
            ps.setInt(3, fineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FineDAO] markPaid error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean waive(int fineId, int librarianId) {
        String sql = "UPDATE fines SET status='WAIVED', collected_by=?, collected_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, librarianId);
            ps.setInt(2, fineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FineDAO] waive error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public BigDecimal totalCollectedToday() {
        String sql = "SELECT COALESCE(SUM(paid_amount), 0) FROM fines WHERE DATE(collected_at) = CURDATE() AND status='PAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("[FineDAO] totalCollectedToday error: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal totalPendingFines() {
        String sql = "SELECT COALESCE(SUM(amount - COALESCE(paid_amount,0)), 0) FROM fines WHERE status='PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("[FineDAO] totalPendingFines error: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine();
        f.setId(rs.getInt("id"));
        f.setIssueId(rs.getInt("issue_id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setAmount(rs.getBigDecimal("amount"));
        f.setPaidAmount(rs.getBigDecimal("paid_amount"));
        f.setStatus(rs.getString("status"));
        f.setCollectedBy(rs.getInt("collected_by"));
        Timestamp ts = rs.getTimestamp("collected_at");
        if (ts != null) f.setCollectedAt(ts.toLocalDateTime());
        f.setStudentName(rs.getString("student_name"));
        f.setStudentIdStr(rs.getString("student_id_str"));
        f.setBookTitle(rs.getString("book_title"));
        return f;
    }
}
