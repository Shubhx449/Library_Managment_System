package dao;

import model.User;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    boolean save(User user);
    boolean updatePassword(int userId, String hashedPassword);
    boolean deactivate(int userId);
}
