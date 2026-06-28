package dao.impl;

import config.DBConnection;
import dao.LibrarianDAO;
import model.Librarian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrarianDAOImpl implements LibrarianDAO {

    private static final String SELECT_FULL =
            "SELECT l.*, u.name, u.email, u.is_active FROM librarians l JOIN users u ON l.user_id = u.id";

    @Override
    public boolean save(Librarian librarian, String hashedPassword) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,'LIBRARIAN')";
            try (PreparedStatement uPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                uPs.setString(1, librarian.getName());
                uPs.setString(2, librarian.getEmail());
                uPs.setString(3, hashedPassword);
                uPs.executeUpdate();
                ResultSet keys = uPs.getGeneratedKeys();
                if (!keys.next()) { conn.rollback(); return false; }
                librarian.setUserId(keys.getInt(1));
            }

            String lSql = "INSERT INTO librarians (user_id, employee_id, phone, address, joined_at) VALUES (?,?,?,?,?)";
            try (PreparedStatement lPs = conn.prepareStatement(lSql, Statement.RETURN_GENERATED_KEYS)) {
                lPs.setInt(1, librarian.getUserId());
                lPs.setString(2, librarian.getEmployeeId());
                lPs.setString(3, librarian.getPhone());
                lPs.setString(4, librarian.getAddress());
                lPs.setDate(5, librarian.getJoinedAt() != null ? Date.valueOf(librarian.getJoinedAt()) : Date.valueOf(java.time.LocalDate.now()));
                lPs.executeUpdate();
                ResultSet lKeys = lPs.getGeneratedKeys();
                if (lKeys.next()) librarian.setId(lKeys.getInt(1));
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] save error: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignored */ }
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignored */ }
        }
        return false;
    }

    @Override
    public Optional<Librarian> findById(int id) {
        return querySingle(SELECT_FULL + " WHERE l.id = ?", id);
    }

    @Override
    public Optional<Librarian> findByUserId(int userId) {
        return querySingle(SELECT_FULL + " WHERE l.user_id = ?", userId);
    }

    @Override
    public List<Librarian> findAll() {
        List<Librarian> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE u.is_active = TRUE ORDER BY u.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Librarian> findByName(String keyword) {
        List<Librarian> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE u.name LIKE ? AND u.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] findByName error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(Librarian librarian) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("UPDATE librarians SET phone=?, address=? WHERE id=?");
             PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET name=?, email=? WHERE id=?")) {
            ps1.setString(1, librarian.getPhone());
            ps1.setString(2, librarian.getAddress());
            ps1.setInt(3, librarian.getId());
            ps1.executeUpdate();

            ps2.setString(1, librarian.getName());
            ps2.setString(2, librarian.getEmail());
            ps2.setInt(3, librarian.getUserId());
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deactivate(int librarianId) {
        String sql = "UPDATE users u JOIN librarians l ON u.id = l.user_id SET u.is_active = FALSE WHERE l.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, librarianId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] deactivate error: " + e.getMessage());
        }
        return false;
    }

    private Optional<Librarian> querySingle(String sql, int param) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[LibrarianDAO] query error: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Librarian mapRow(ResultSet rs) throws SQLException {
        Librarian l = new Librarian();
        l.setId(rs.getInt("id"));
        l.setUserId(rs.getInt("user_id"));
        l.setName(rs.getString("name"));
        l.setEmail(rs.getString("email"));
        l.setEmployeeId(rs.getString("employee_id"));
        l.setPhone(rs.getString("phone"));
        l.setAddress(rs.getString("address"));
        Date joined = rs.getDate("joined_at");
        if (joined != null) l.setJoinedAt(joined.toLocalDate());
        l.setActive(rs.getBoolean("is_active"));
        return l;
    }
}
