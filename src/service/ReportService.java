package service;

import dao.ReportDAO;
import dao.impl.ReportDAOImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAOImpl();

    public void showMostIssuedBooks() {
        System.out.println("\n  ══ Most Issued Books (Top 10) ══");
        LinkedHashMap<String, Integer> data = reportDAO.mostIssuedBooks(10);
        if (data.isEmpty()) { System.out.println("  No data available."); return; }
        int rank = 1;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            System.out.printf("  %2d. %-40s %d issue(s)%n", rank++, truncate(e.getKey(), 40), e.getValue());
        }
    }

    public void showFineCollectionReport() {
        System.out.println("\n  ══ Fine Collection Report (Last 30 Days) ══");
        LinkedHashMap<String, Double> data = reportDAO.fineCollectionByDay(30);
        if (data.isEmpty()) { System.out.println("  No fines collected in the last 30 days."); return; }
        double total = 0;
        for (Map.Entry<String, Double> e : data.entrySet()) {
            System.out.printf("  %-12s : ₹%.2f%n", e.getKey(), e.getValue());
            total += e.getValue();
        }
        System.out.printf("  %-12s : ₹%.2f%n", "TOTAL", total);
    }

    public void showActiveStudentsReport() {
        System.out.println("\n  ══ Most Active Students (Top 10) ══");
        LinkedHashMap<String, Integer> data = reportDAO.activeStudents(10);
        if (data.isEmpty()) { System.out.println("  No data available."); return; }
        int rank = 1;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            System.out.printf("  %2d. %-35s %d borrow(s)%n", rank++, truncate(e.getKey(), 35), e.getValue());
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
