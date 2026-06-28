package service;

import dao.CategoryDAO;
import dao.impl.CategoryDAOImpl;
import model.Category;

import java.util.List;
import java.util.Optional;

public class CategoryService {

    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    public void addCategory(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        Category category = new Category(name.trim(), description != null ? description.trim() : "");
        if (!categoryDAO.save(category)) {
            throw new RuntimeException("Failed to add category. Name may already exist.");
        }
        System.out.println("  [✓] Category '" + name + "' added successfully.");
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }

    public void updateCategory(int id, String newName, String newDescription) {
        Category category = getCategoryById(id);
        category.setName(newName.trim());
        category.setDescription(newDescription.trim());
        if (!categoryDAO.update(category)) {
            throw new RuntimeException("Failed to update category.");
        }
        System.out.println("  [✓] Category updated successfully.");
    }

    public void deleteCategory(int id) {
        getCategoryById(id); // validates existence
        if (!categoryDAO.delete(id)) {
            throw new RuntimeException("Cannot delete category. It may be assigned to books.");
        }
        System.out.println("  [✓] Category deleted successfully.");
    }

    public void printAllCategories() {
        List<Category> list = getAllCategories();
        if (list.isEmpty()) {
            System.out.println("  No categories found.");
            return;
        }
        System.out.println("\n  +----+----------------------+--------------------------------+");
        System.out.printf("  | %-3s| %-20s | %-30s |%n", "ID", "Name", "Description");
        System.out.println("  +----+----------------------+--------------------------------+");
        for (Category c : list) {
            System.out.printf("  | %-3d| %-20s | %-30s |%n",
                    c.getId(),
                    truncate(c.getName(), 20),
                    truncate(c.getDescription(), 30));
        }
        System.out.println("  +----+----------------------+--------------------------------+");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
