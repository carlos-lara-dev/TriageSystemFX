package cruds;

import triagesystem.SqlConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ReporteCRUD {

    public double getPromedioEsperaMinutos(String desde, String hasta) {
        String sql =
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, i.created_at, c.hora_inicio)) " +
            "FROM ingreso i JOIN consulta c ON c.id_ingreso = i.id_ingreso " +
            "WHERE DATE(i.created_at) BETWEEN ? AND ? AND i.activo = 1 AND c.activo = 1";
        return consultarDouble(sql, desde, hasta);
    }

    public double getPromedioConsultaMinutos(String desde, String hasta) {
        String sql =
            "SELECT AVG(TIMESTAMPDIFF(MINUTE, c.hora_inicio, c.hora_fin)) " +
            "FROM consulta c JOIN ingreso i ON i.id_ingreso = c.id_ingreso " +
            "WHERE DATE(i.created_at) BETWEEN ? AND ? AND c.hora_fin IS NOT NULL " +
            "  AND i.activo = 1 AND c.activo = 1";
        return consultarDouble(sql, desde, hasta);
    }

    public int getTotalAtendidos(String desde, String hasta) {
        String sql =
            "SELECT COUNT(*) FROM ingreso " +
            "WHERE id_estado = 3 AND DATE(created_at) BETWEEN ? AND ? AND activo = 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<Integer, Integer> getIngresosPorHora(String desde, String hasta) {
        Map<Integer, Integer> map = new TreeMap<>();
        String sql =
            "SELECT HOUR(created_at) AS hora, COUNT(*) AS total " +
            "FROM ingreso " +
            "WHERE DATE(created_at) BETWEEN ? AND ? AND activo = 1 " +
            "GROUP BY hora ORDER BY hora";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getInt("hora"), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public Map<String, Integer> getTopMedicos(String desde, String hasta) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            "SELECT m.nombre, COUNT(*) AS total " +
            "FROM consulta c " +
            "JOIN medico m ON m.id_medico = c.id_medico " +
            "JOIN ingreso i ON i.id_ingreso = c.id_ingreso " +
            "WHERE DATE(i.created_at) BETWEEN ? AND ? AND c.activo = 1 " +
            "GROUP BY m.id_medico, m.nombre ORDER BY total DESC LIMIT 10";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getString("nombre"), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public Map<String, Integer> getIngresosPorPrioridad(String desde, String hasta) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            "SELECT pr.nombre, COUNT(*) AS total " +
            "FROM ingreso i JOIN prioridad pr ON pr.id_prioridad = i.id_prioridad " +
            "WHERE DATE(i.created_at) BETWEEN ? AND ? AND i.activo = 1 " +
            "GROUP BY pr.id_prioridad, pr.nombre ORDER BY pr.valor DESC";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getString("nombre"), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    private double consultarDouble(String sql, String desde, String hasta) {
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }
}
