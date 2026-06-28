package service;

import dao.BookDAO;
import dao.ReservationDAO;
import dao.StudentDAO;
import dao.impl.BookDAOImpl;
import dao.impl.ReservationDAOImpl;
import dao.impl.StudentDAOImpl;
import exception.BookNotFoundException;
import exception.StudentNotFoundException;
import model.Book;
import model.Reservation;
import model.Student;
import util.DateUtil;

import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAOImpl();
    private final BookDAO        bookDAO        = new BookDAOImpl();
    private final StudentDAO     studentDAO     = new StudentDAOImpl();

    public void reserveBook(int studentId, int bookId) {
        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));

        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found."));

        if (book.isAvailable())
            throw new IllegalStateException("Book is currently available. You can borrow it directly.");

        if (reservationDAO.hasActiveReservation(studentId, bookId))
            throw new IllegalStateException("You already have an active reservation for this book.");

        Reservation reservation = new Reservation();
        reservation.setStudentId(studentId);
        reservation.setBookId(bookId);
        reservation.setExpiryDate(DateUtil.reservationExpiry());
        reservation.setStatus("PENDING");

        if (!reservationDAO.save(reservation))
            throw new RuntimeException("Failed to create reservation.");

        System.out.println("  [✓] Reservation created successfully!");
        System.out.println("      Book       : " + book.getTitle());
        System.out.println("      Student    : " + student.getName());
        System.out.println("      Expires on : " + DateUtil.format(reservation.getExpiryDate()));
    }

    public void cancelReservation(int reservationId, int studentId) {
        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        if (reservation.getStudentId() != studentId)
            throw new SecurityException("You can only cancel your own reservations. Reservation belongs to another student.");

        if (!"PENDING".equals(reservation.getStatus()))
            throw new IllegalStateException("Only PENDING reservations can be cancelled.");

        if (!reservationDAO.updateStatus(reservationId, "CANCELLED"))
            throw new RuntimeException("Failed to cancel reservation.");

        System.out.println("  [✓] Reservation cancelled successfully.");
    }

    public void fulfillReservation(int reservationId) {
        reservationDAO.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        reservationDAO.updateStatus(reservationId, "FULFILLED");
    }

    public List<Reservation> getPendingReservations()               { return reservationDAO.findPending(); }
    public List<Reservation> getStudentReservations(int studentId)  { return reservationDAO.findByStudent(studentId); }
    public List<Reservation> getReservationsForBook(int bookId)     { return reservationDAO.findByBook(bookId); }

    public void printReservations(List<Reservation> list) {
        if (list.isEmpty()) { System.out.println("  No reservations found."); return; }
        System.out.println("\n  +-----+----------------------+---------------------------+------------+----------+");
        System.out.printf("  | %-4s| %-20s | %-25s | %-10s | %-8s |%n",
                "ID", "Student", "Book", "Expiry", "Status");
        System.out.println("  +-----+----------------------+---------------------------+------------+----------+");
        for (Reservation r : list) {
            System.out.printf("  | %-4d| %-20s | %-25s | %-10s | %-8s |%n",
                    r.getId(),
                    truncate(r.getStudentName(), 20),
                    truncate(r.getBookTitle(), 25),
                    DateUtil.format(r.getExpiryDate()),
                    r.getStatus());
        }
        System.out.println("  +-----+----------------------+---------------------------+------------+----------+");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
