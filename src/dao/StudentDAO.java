package dao;

import model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDAO {
    boolean save(Student student, String hashedPassword);
    Optional<Student> findById(int id);
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(int userId);
    List<Student> findAll();
    List<Student> findByName(String keyword);
    boolean update(Student student);
    boolean deactivate(int studentId);
    int countCurrentlyIssuedBooks(int studentId);
}
