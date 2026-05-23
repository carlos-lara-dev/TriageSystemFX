package cruds;

import pojos.Consulta;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaCRUD {

    public List<Consulta> getAll() {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id, id_ingreso, id_medico, hora_inicio, hora_fin, observaciones, activo " +
                     "FROM consulta WHERE activo = 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Consulta getById(int id) {
        String sql = "SELECT id, id_ingreso, id_medico, hora_inicio, hora_fin, observaciones, activo " +
                     "FROM consulta WHERE id = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Consulta> getByIngreso(int idIngreso) {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id, id_ingreso, id_medico, hora_inicio, hora_fin, observaciones, activo " +
                     "FROM consulta WHERE id_ingreso = ? AND activo = 1 ORDER BY id";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idIngreso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Consulta getConsultaActivaPorMedico(int idMedico) {
        String sql = "SELECT c.id, c.id_ingreso, c.id_medico, c.hora_inicio, c.hora_fin, " +
                     "       c.observaciones, c.activo " +
                     "FROM consulta c " +
                     "JOIN ingreso i ON i.id_ingreso = c.id_ingreso " +
                     "WHERE c.id_medico = ? AND c.hora_fin IS NULL AND c.activo = 1 " +
                     "  AND i.id_estado = 2 " +
                     "ORDER BY c.id DESC LIMIT 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Consulta consulta) {
        String sql = "INSERT INTO consulta (id_ingreso, id_medico, hora_inicio, hora_fin, observaciones, activo) " +
                     "VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, consulta.getId_ingreso());
            ps.setInt(2, consulta.getId_medico());
            ps.setString(3, consulta.getHora_inicio());
            if (consulta.getHora_fin() == null || consulta.getHora_fin().isEmpty())
                ps.setNull(4, Types.TIMESTAMP);
            else
                ps.setString(4, consulta.getHora_fin());
            ps.setString(5, consulta.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Consulta consulta) {
        String sql = "UPDATE consulta SET id_medico=?, hora_fin=?, observaciones=? WHERE id=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, consulta.getId_medico());
            if (consulta.getHora_fin() == null || consulta.getHora_fin().isEmpty())
                ps.setNull(2, Types.TIMESTAMP);
            else
                ps.setString(2, consulta.getHora_fin());
            ps.setString(3, consulta.getObservaciones());
            ps.setInt(4, consulta.getId_consulta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE consulta SET activo = 0 WHERE id = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Consulta mapear(ResultSet rs) throws SQLException {
        return new Consulta(
            rs.getInt("id"),
            rs.getInt("id_ingreso"),
            rs.getInt("id_medico"),
            rs.getString("hora_inicio"),
            rs.getString("hora_fin"),
            rs.getString("observaciones"),
            rs.getBoolean("activo")
        );
    }
}
