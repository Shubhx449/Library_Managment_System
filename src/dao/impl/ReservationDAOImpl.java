package dao.impl;

import config.DBConnection;
import dao.ReservationDAO;
import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {

    private static final String SELECT_FULL =
            "SELECT r.*, u.name AS student_name, b.title AS book_title, b.isbn AS book_isbn " +
                    "FROM reservations r " +
                    "JOIN students s ON r.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "JOIN books b ON r.book_id = b.id";

    @Override
    public boolean save(Reservation reservation) {
        String sql = "INSERT INTO reservations (student_id, book_id, expiry_date, status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getStudentId());
            ps.setInt(2, reservation.getBookId());
            ps.setDate(3, Date.valueOf(reservation.getExpiryDate()));
            ps.setString(4, "PENDING");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) reservation.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] save error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Reservation> findById(int id) {
        String sql = SELECT_FULL + " WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findByStudent(int studentId) {
        List<Reservation> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE r.student_id = ? ORDER BY r.reserved_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findByStudent error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Reservation> findPending() {
        List<Reservation> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE r.status = 'PENDING' ORDER BY r.reserved_at";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findPending error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Reservation> findByBook(int bookId) {
        List<Reservation> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE r.book_id = ? AND r.status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findByBook error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean hasActiveReservation(int studentId, int bookId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE student_id=? AND book_id=? AND status='PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] hasActiveReservation error: " + e.getMessage());
        }
        return false;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setBookId(rs.getInt("book_id"));
        r.setStatus(rs.getString("status"));
        Date expiry = rs.getDate("expiry_date");
        if (expiry != null) r.setExpiryDate(expiry.toLocalDate());
        Timestamp ts = rs.getTimestamp("reserved_at");
        if (ts != null) r.setReservedAt(ts.toLocalDateTime());
        r.setStudentName(rs.getString("student_name"));
        r.setBookTitle(rs.getString("book_title"));
        r.setBookIsbn(rs.getString("book_isbn"));
        return r;
    }
}
