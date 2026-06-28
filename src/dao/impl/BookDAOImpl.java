package dao.impl;

import config.DBConnection;
import dao.BookDAO;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    private static final String SELECT_WITH_CATEGORY =
            "SELECT b.*, c.name AS category_name FROM books b " +
                    "LEFT JOIN categories c ON b.category_id = c.id";

    @Override
    public boolean save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category_id, publisher, publish_year, " +
                "total_copies, available_copies, shelf_location) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getCategoryId());
            ps.setString(5, book.getPublisher());
            ps.setInt(6, book.getPublishYear());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());
            ps.setString(9, book.getShelfLocation());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) book.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] save error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = SELECT_WITH_CATEGORY + " WHERE b.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = SELECT_WITH_CATEGORY + " WHERE b.isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findByIsbn error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " ORDER BY b.title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findAll error: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findByTitle(String keyword) {
        return searchBy("b.title", keyword);
    }

    @Override
    public List<Book> findByAuthor(String keyword) {
        return searchBy("b.author", keyword);
    }

    private List<Book> searchBy(String column, String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE " + column + " LIKE ? ORDER BY b.title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] searchBy error: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE b.category_id = ? ORDER BY b.title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findByCategory error: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findAvailable() {
        List<Book> books = new ArrayList<>();
        String sql = SELECT_WITH_CATEGORY + " WHERE b.available_copies > 0 ORDER BY b.title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] findAvailable error: " + e.getMessage());
        }
        return books;
    }

    @Override
    public boolean update(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, category_id=?, publisher=?, " +
                "publish_year=?, total_copies=?, available_copies=?, shelf_location=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getCategoryId());
            ps.setString(5, book.getPublisher());
            ps.setInt(6, book.getPublishYear());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());
            ps.setString(9, book.getShelfLocation());
            ps.setInt(10, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean decrementAvailable(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 " +
                "WHERE id = ? AND available_copies > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] decrementAvailable error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean incrementAvailable(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 " +
                "WHERE id = ? AND available_copies < total_copies";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] incrementAvailable error: " + e.getMessage());
        }
        return false;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setIsbn(rs.getString("isbn"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setPublisher(rs.getString("publisher"));
        b.setPublishYear(rs.getInt("publish_year"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setShelfLocation(rs.getString("shelf_location"));
        return b;
    }
}
