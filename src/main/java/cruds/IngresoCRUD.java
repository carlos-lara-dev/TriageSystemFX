package cruds;

import pojos.Ingreso;
import pojos.IngresoDetalle;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngresoCRUD {

    public List<IngresoDetalle> getAllDetalle() {
        List<IngresoDetalle> lista = new ArrayList<>();
        String sql =
            "SELECT i.id_ingreso, i.id_estado, p.nombre AS nombre_paciente, p.dpi, " +
            "       pr.nombre AS nombre_prioridad, e.nombre AS nombre_estado, " +
            "       IFNULL(c.id_medico, 0) AS id_medico_consulta, " +
            "       IFNULL(m.nombre, '—') AS nombre_medico, " +
            "       i.sintomas, i.created_at " +
            "FROM ingreso i " +
            "JOIN paciente p  ON p.id_paciente  = i.id_paciente " +
            "JOIN prioridad pr ON pr.id_prioridad = i.id_prioridad " +
            "JOIN estado e    ON e.id_estado    = i.id_estado " +
            "LEFT JOIN (SELECT id_ingreso, MAX(id) AS max_id " +
            "           FROM consulta WHERE activo = 1 GROUP BY id_ingreso) ult " +
            "    ON ult.id_ingreso = i.id_ingreso " +
            "LEFT JOIN consulta c ON c.id = ult.max_id " +
            "LEFT JOIN medico m   ON m.id_medico = c.id_medico " +
            "WHERE i.activo = 1 " +
            "ORDER BY pr.valor DESC, i.created_at ASC";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new IngresoDetalle(
                    rs.getInt("id_ingreso"),
                    rs.getInt("id_estado"),
                    rs.getInt("id_medico_consulta"),
                    rs.getString("nombre_paciente"),
                    rs.getString("dpi"),
                    rs.getString("nombre_prioridad"),
                    rs.getString("nombre_estado"),
                    rs.getString("nombre_medico"),
                    rs.getString("sintomas"),
                    rs.getString("created_at")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insert(Ingreso ingreso) {
        String sql = "INSERT INTO ingreso (id_paciente, id_prioridad, id_estado, sintomas, activo) " +
                     "VALUES (?, ?, ?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ingreso.getId_paciente());
            ps.setInt(2, ingreso.getId_prioridad());
            ps.setInt(3, ingreso.getId_estado());
            ps.setString(4, ingreso.getSintomas());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateEstado(int idIngreso, int idEstado) {
        String sql = "UPDATE ingreso SET id_estado = ? WHERE id_ingreso = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setInt(2, idIngreso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int idIngreso) {
        String sql = "UPDATE ingreso SET activo = 0 WHERE id_ingreso = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idIngreso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
