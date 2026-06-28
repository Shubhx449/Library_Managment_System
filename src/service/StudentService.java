package service;

import dao.StudentDAO;
import dao.impl.StudentDAOImpl;
import exception.StudentNotFoundException;
import model.Student;
import util.PasswordUtil;
import util.ValidationUtil;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAOImpl();

    public void registerStudent(Student student, String password) {
        if (!ValidationUtil.isNotBlank(student.getName()))
            throw new IllegalArgumentException("Student name cannot be empty.");
        if (!ValidationUtil.isValidEmail(student.getEmail()))
            throw new IllegalArgumentException("Invalid email address.");
        if (!ValidationUtil.isNotBlank(student.getStudentId()))
            throw new IllegalArgumentException("Student ID cannot be empty.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (student.getSemester() < 1 || student.getSemester() > 8)
            throw new IllegalArgumentException("Semester must be between 1 and 8.");

        String hashed = PasswordUtil.hash(password);
        if (!studentDAO.save(student, hashed))
            throw new RuntimeException("Failed to register student. Email or Student ID may already exist.");

        System.out.println("  [✓] Student '" + student.getName() + "' registered. ID: " + student.getStudentId());
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public Student getStudentById(int id) {
        return studentDAO.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
    }

    public Student getStudentByStudentId(String studentId) {
        return studentDAO.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("No student found with ID: " + studentId));
    }

    public Student getStudentByUserId(int userId) {
        return studentDAO.findByUserId(userId)
                .orElseThrow(() -> new StudentNotFoundException("Student profile not found."));
    }

    public List<Student> searchByName(String keyword) {
        return studentDAO.findByName(keyword);
    }

    public void updateStudent(Student student) {
        getStudentById(student.getId()); // validate existence
        if (!studentDAO.update(student))
            throw new RuntimeException("Failed to update student.");
        System.out.println("  [✓] Student updated successfully.");
    }

    public void deleteStudent(int id) {
        getStudentById(id); // validate existence
        if (!studentDAO.deactivate(id))
            throw new RuntimeException("Failed to deactivate student.");
        System.out.println("  [✓] Student account deactivated.");
    }

    public int getCurrentlyIssuedCount(int studentId) {
        return studentDAO.countCurrentlyIssuedBooks(studentId);
    }

    public void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("  No students found.");
            return;
        }
        System.out.println("\n  +-----+------------+----------------------+----------------------+----------+------+");
        System.out.printf("  | %-4s| %-10s | %-20s | %-20s | %-8s | %-4s |%n",
                "ID", "Student ID", "Name", "Email", "Dept", "Sem");
        System.out.println("  +-----+------------+----------------------+----------------------+----------+------+");
        for (Student s : students) {
            System.out.printf("  | %-4d| %-10s | %-20s | %-20s | %-8s | %-4d |%n",
                    s.getId(),
                    s.getStudentId(),
                    truncate(s.getName(), 20),
                    truncate(s.getEmail(), 20),
                    truncate(s.getDepartment(), 8),
                    s.getSemester());
        }
        System.out.println("  +-----+------------+----------------------+----------------------+----------+------+");
        System.out.println("  Total: " + students.size() + " student(s)");
    }

    public void printStudentDetails(Student s) {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║         STUDENT DETAILS              ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf("  ║  %-12s : %-22s║%n", "Student ID",  s.getStudentId());
        System.out.printf("  ║  %-12s : %-22s║%n", "Name",        s.getName());
        System.out.printf("  ║  %-12s : %-22s║%n", "Email",       s.getEmail());
        System.out.printf("  ║  %-12s : %-22s║%n", "Phone",       s.getPhone() != null ? s.getPhone() : "N/A");
        System.out.printf("  ║  %-12s : %-22s║%n", "Department",  s.getDepartment());
        System.out.printf("  ║  %-12s : %-22d║%n", "Semester",    s.getSemester());
        System.out.printf("  ║  %-12s : %-22d║%n", "Max Books",   s.getMaxBooks());
        System.out.printf("  ║  %-12s : %-22s║%n", "Status",      s.isActive() ? "Active" : "Inactive");
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
