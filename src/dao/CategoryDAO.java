package dao;

import model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    boolean save(Category category);
    Optional<Category> findById(int id);
    List<Category> findAll();
    boolean update(Category category);
    boolean delete(int id);
}
