package dao;

import java.util.LinkedHashMap;

public interface ReportDAO {
    LinkedHashMap<String, Integer> mostIssuedBooks(int limit);


    LinkedHashMap<String, Integer> activeStudents(int limit);


    LinkedHashMap<String, Double> fineCollectionByDay(int days);

    int totalBooks();
    int totalAvailableBooks();
    int totalIssuedBooks();
    int totalStudents();
    int totalLibrarians();
    int totalOverdueBooks();
    int todayTransactions();
}
