package cruds;

import pojos.Medico;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoCRUD {

    public List<Medico> getAll() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT id_medico, nombre, activo FROM medico WHERE activo = 1 ORDER BY nombre";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Medico getById(int id) {
        String sql = "SELECT id_medico, nombre, activo FROM medico WHERE id_medico = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Medico m) {
        String sql = "INSERT INTO medico (nombre, activo) VALUES (?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Medico m) {
        String sql = "UPDATE medico SET nombre=?, activo=? WHERE id_medico=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setBoolean(2, m.isActivo());
            ps.setInt(3, m.getId_medico());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Medico> getMedicosDisponibles() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT m.id_medico, m.nombre, m.activo FROM medico m " +
                     "WHERE m.activo = 1 AND m.id_medico NOT IN (" +
                     "    SELECT c.id_medico FROM consulta c " +
                     "    JOIN ingreso i ON i.id_ingreso = c.id_ingreso " +
                     "    WHERE i.id_estado = 2 AND c.activo = 1" +
                     ") ORDER BY m.nombre";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean delete(int id) {
        String sql = "UPDATE medico SET activo = 0 WHERE id_medico = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Medico mapear(ResultSet rs) throws SQLException {
        return new Medico(
            rs.getInt("id_medico"),
            rs.getString("nombre"),
            rs.getBoolean("activo")
        );
    }
}
