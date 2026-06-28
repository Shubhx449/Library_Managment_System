package dao;

import model.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    boolean save(Reservation reservation);
    Optional<Reservation> findById(int id);
    List<Reservation> findByStudent(int studentId);
    List<Reservation> findPending();
    List<Reservation> findByBook(int bookId);
    boolean updateStatus(int reservationId, String status);
    boolean hasActiveReservation(int studentId, int bookId);
}
