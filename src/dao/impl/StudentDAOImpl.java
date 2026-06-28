package dao.impl;

import config.DBConnection;
import dao.StudentDAO;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    private static final String SELECT_FULL =
            "SELECT s.*, u.name, u.email, u.is_active FROM students s " +
                    "JOIN users u ON s.user_id = u.id";

    @Override
    public boolean save(Student student, String hashedPassword) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into users
            String userSql = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,'STUDENT')";
            try (PreparedStatement userPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPs.setString(1, student.getName());
                userPs.setString(2, student.getEmail());
                userPs.setString(3, hashedPassword);
                userPs.executeUpdate();
                ResultSet keys = userPs.getGeneratedKeys();
                if (!keys.next()) { conn.rollback(); return false; }
                student.setUserId(keys.getInt(1));
            }

            // 2. Insert into students
            String stSql = "INSERT INTO students (user_id, student_id, phone, department, semester) VALUES (?,?,?,?,?)";
            try (PreparedStatement stPs = conn.prepareStatement(stSql, Statement.RETURN_GENERATED_KEYS)) {
                stPs.setInt(1, student.getUserId());
                stPs.setString(2, student.getStudentId());
                stPs.setString(3, student.getPhone());
                stPs.setString(4, student.getDepartment());
                stPs.setInt(5, student.getSemester());
                stPs.executeUpdate();
                ResultSet stKeys = stPs.getGeneratedKeys();
                if (stKeys.next()) student.setId(stKeys.getInt(1));
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] save error: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignored */ }
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignored */ }
        }
        return false;
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = SELECT_FULL + " WHERE s.id = ?";
        return querySingle(sql, id);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        String sql = SELECT_FULL + " WHERE s.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findByStudentId error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByUserId(int userId) {
        String sql = SELECT_FULL + " WHERE s.user_id = ?";
        return querySingle(sql, userId);
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE u.is_active = TRUE ORDER BY u.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Student> findByName(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE u.name LIKE ? AND u.is_active = TRUE ORDER BY u.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findByName error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET phone=?, department=?, semester=? WHERE id=?";
        String userSql = "UPDATE users SET name=?, email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(sql);
             PreparedStatement ps2 = conn.prepareStatement(userSql)) {
            ps1.setString(1, student.getPhone());
            ps1.setString(2, student.getDepartment());
            ps1.setInt(3, student.getSemester());
            ps1.setInt(4, student.getId());
            ps1.executeUpdate();

            ps2.setString(1, student.getName());
            ps2.setString(2, student.getEmail());
            ps2.setInt(3, student.getUserId());
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deactivate(int studentId) {
        String sql = "UPDATE users u JOIN students s ON u.id = s.user_id " +
                "SET u.is_active = FALSE WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] deactivate error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int countCurrentlyIssuedBooks(int studentId) {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE student_id = ? AND status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] countCurrentlyIssuedBooks error: " + e.getMessage());
        }
        return 0;
    }

    private Optional<Student> querySingle(String sql, int param) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO] query error: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setStudentId(rs.getString("student_id"));
        s.setPhone(rs.getString("phone"));
        s.setDepartment(rs.getString("department"));
        s.setSemester(rs.getInt("semester"));
        s.setMaxBooks(rs.getInt("max_books"));
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }
}
