package dao;

import model.Book;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    boolean save(Book book);
    Optional<Book> findById(int id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> findByTitle(String keyword);
    List<Book> findByAuthor(String keyword);
    List<Book> findByCategory(int categoryId);
    List<Book> findAvailable();
    boolean update(Book book);
    boolean delete(int id);
    boolean decrementAvailable(int bookId);
    boolean incrementAvailable(int bookId);
}
