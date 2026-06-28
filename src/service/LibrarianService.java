package service;

import dao.LibrarianDAO;
import dao.impl.LibrarianDAOImpl;
import model.Librarian;
import util.PasswordUtil;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

public class LibrarianService {

    private final LibrarianDAO librarianDAO = new LibrarianDAOImpl();

    public void addLibrarian(Librarian librarian, String password) {
        if (!ValidationUtil.isNotBlank(librarian.getName()))
            throw new IllegalArgumentException("Name cannot be empty.");
        if (!ValidationUtil.isValidEmail(librarian.getEmail()))
            throw new IllegalArgumentException("Invalid email address.");
        if (!ValidationUtil.isNotBlank(librarian.getEmployeeId()))
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");

        if (librarian.getJoinedAt() == null)
            librarian.setJoinedAt(LocalDate.now());

        String hashed = PasswordUtil.hash(password);
        if (!librarianDAO.save(librarian, hashed))
            throw new RuntimeException("Failed to add librarian. Email or Employee ID may already exist.");

        System.out.println("  [✓] Librarian '" + librarian.getName() + "' added successfully.");
    }

    public List<Librarian> getAllLibrarians() {
        return librarianDAO.findAll();
    }

    public Librarian getLibrarianById(int id) {
        return librarianDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Librarian not found with ID: " + id));
    }

    public Librarian getLibrarianByUserId(int userId) {
        return librarianDAO.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Librarian profile not found."));
    }

    public List<Librarian> searchByName(String keyword) {
        return librarianDAO.findByName(keyword);
    }

    public void updateLibrarian(Librarian librarian) {
        getLibrarianById(librarian.getId());
        if (!librarianDAO.update(librarian))
            throw new RuntimeException("Failed to update librarian.");
        System.out.println("  [✓] Librarian updated successfully.");
    }

    public void deleteLibrarian(int id) {
        getLibrarianById(id);
        if (!librarianDAO.deactivate(id))
            throw new RuntimeException("Failed to deactivate librarian.");
        System.out.println("  [✓] Librarian account deactivated.");
    }

    public void printLibrarians(List<Librarian> list) {
        if (list.isEmpty()) {
            System.out.println("  No librarians found.");
            return;
        }
        System.out.println("\n  +-----+------------+----------------------+----------------------+");
        System.out.printf("  | %-4s| %-10s | %-20s | %-20s |%n", "ID", "Emp ID", "Name", "Email");
        System.out.println("  +-----+------------+----------------------+----------------------+");
        for (Librarian l : list) {
            System.out.printf("  | %-4d| %-10s | %-20s | %-20s |%n",
                    l.getId(),
                    l.getEmployeeId(),
                    truncate(l.getName(), 20),
                    truncate(l.getEmail(), 20));
        }
        System.out.println("  +-----+------------+----------------------+----------------------+");
        System.out.println("  Total: " + list.size() + " librarian(s)");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
