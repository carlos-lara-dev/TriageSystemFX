package triagesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    private static Connection conn;
    private static final String URL  = "jdbc:mysql://kodama.proxy.rlwy.net:22227/triage_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "AdwdHGptQcFRLQKprhGOntayodOhdAwm"; // "428003";

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