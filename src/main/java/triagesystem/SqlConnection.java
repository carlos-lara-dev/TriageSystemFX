package triagesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    private static Connection conn;
    private static final String URL  = "jdbc:mysql://207.180.202.235:3306/triage_system?useSSL=false&connectionTimeZone=GMT-6&forceConnectionTimeZoneToSession=true";
    private static final String USER = "public-user"; // "root";
    private static final String PASS = "1234!"; // "428003";

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión exitosa");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conn;
    }
}
//%I}kL:1/>~O(cse-