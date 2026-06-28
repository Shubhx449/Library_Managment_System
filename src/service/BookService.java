package service;

import dao.BookDAO;
import dao.impl.BookDAOImpl;
import exception.BookNotFoundException;
import model.Book;
import util.ValidationUtil;

import java.util.List;

public class BookService {

    private final BookDAO bookDAO = new BookDAOImpl();

    public void addBook(Book book) {
        if (!ValidationUtil.isNotBlank(book.getTitle()))
            throw new IllegalArgumentException("Book title cannot be empty.");
        if (!ValidationUtil.isNotBlank(book.getAuthor()))
            throw new IllegalArgumentException("Author cannot be empty.");
        if (!ValidationUtil.isValidIsbn(book.getIsbn()))
            throw new IllegalArgumentException("Invalid ISBN format.");
        if (!ValidationUtil.isPositive(book.getTotalCopies()))
            throw new IllegalArgumentException("Total copies must be at least 1.");

        if (bookDAO.findByIsbn(book.getIsbn()).isPresent())
            throw new IllegalArgumentException("A book with ISBN '" + book.getIsbn() + "' already exists.");

        book.setAvailableCopies(book.getTotalCopies());

        if (!bookDAO.save(book))
            throw new RuntimeException("Failed to save book. Please try again.");

        System.out.println("  [✓] Book '" + book.getTitle() + "' added successfully. ID: " + book.getId());
    }

    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailable();
    }

    public Book getBookById(int id) {
        return bookDAO.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    public Book getBookByIsbn(String isbn) {
        return bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    public List<Book> searchByTitle(String keyword) {
        return bookDAO.findByTitle(keyword);
    }

    public List<Book> searchByAuthor(String keyword) {
        return bookDAO.findByAuthor(keyword);
    }

    public List<Book> searchByCategory(int categoryId) {
        return bookDAO.findByCategory(categoryId);
    }

    public void updateBook(Book book) {
        getBookById(book.getId()); // validate existence
        if (!bookDAO.update(book))
            throw new RuntimeException("Failed to update book.");
        System.out.println("  [✓] Book updated successfully.");
    }

    public void deleteBook(int id) {
        getBookById(id); // validate existence
        if (!bookDAO.delete(id))
            throw new RuntimeException("Cannot delete book. It may have active issues.");
        System.out.println("  [✓] Book deleted successfully.");
    }

    public void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("  No books found.");
            return;
        }
        System.out.println("\n  +-----+----------------------------------+----------------------+---------------+----------+-----------+");
        System.out.printf("  | %-4s| %-32s | %-20s | %-13s | %-8s | %-9s |%n",
                "ID", "Title", "Author", "ISBN", "Avail", "Category");
        System.out.println("  +-----+----------------------------------+----------------------+---------------+----------+-----------+");
        for (Book b : books) {
            System.out.printf("  | %-4d| %-32s | %-20s | %-13s | %-3d/%-4d | %-9s |%n",
                    b.getId(),
                    truncate(b.getTitle(), 32),
                    truncate(b.getAuthor(), 20),
                    b.getIsbn(),
                    b.getAvailableCopies(),
                    b.getTotalCopies(),
                    truncate(b.getCategoryName(), 9));
        }
        System.out.println("  +-----+----------------------------------+----------------------+---------------+----------+-----------+");
        System.out.println("  Total: " + books.size() + " book(s)");
    }

    public void printBookDetails(Book b) {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║           BOOK DETAILS               ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf("  ║  %-10s : %-24s║%n", "ID",        b.getId());
        System.out.printf("  ║  %-10s : %-24s║%n", "Title",     truncate(b.getTitle(), 24));
        System.out.printf("  ║  %-10s : %-24s║%n", "Author",    truncate(b.getAuthor(), 24));
        System.out.printf("  ║  %-10s : %-24s║%n", "ISBN",      b.getIsbn());
        System.out.printf("  ║  %-10s : %-24s║%n", "Category",  b.getCategoryName() != null ? b.getCategoryName() : "N/A");
        System.out.printf("  ║  %-10s : %-24s║%n", "Publisher", truncate(b.getPublisher(), 24));
        System.out.printf("  ║  %-10s : %-24d║%n", "Year",      b.getPublishYear());
        System.out.printf("  ║  %-10s : %-3d / %-18d║%n", "Copies",  b.getAvailableCopies(), b.getTotalCopies());
        System.out.printf("  ║  %-10s : %-24s║%n", "Shelf",     b.getShelfLocation() != null ? b.getShelfLocation() : "N/A");
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
