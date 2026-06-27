package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static final String URL      = "jdbc:mysql://127.0.0.1:3306/library_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "NewPassword@123";

    private static Connection connection;


    private DBConnection() {}


    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setAutoCommit(true);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
        }
        return connection;
    }


    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}