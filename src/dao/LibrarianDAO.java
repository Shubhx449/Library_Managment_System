package dao;

import model.Librarian;
import java.util.List;
import java.util.Optional;

public interface LibrarianDAO {
    boolean save(Librarian librarian, String hashedPassword);
    Optional<Librarian> findById(int id);
    Optional<Librarian> findByUserId(int userId);
    List<Librarian> findAll();
    List<Librarian> findByName(String keyword);
    boolean update(Librarian librarian);
    boolean deactivate(int librarianId);
}
