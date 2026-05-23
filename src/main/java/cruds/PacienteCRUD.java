package cruds;

import pojos.Paciente;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteCRUD {

    public List<Paciente> getAll() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id_paciente, nombre, fecha_nacimiento, dpi, telefono, created_at, activo " +
                     "FROM paciente WHERE activo = 1 ORDER BY nombre";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Paciente getById(int id) {
        String sql = "SELECT id_paciente, nombre, fecha_nacimiento, dpi, telefono, created_at, activo " +
                     "FROM paciente WHERE id_paciente = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Paciente p) {
        String sql = "INSERT INTO paciente (nombre, fecha_nacimiento, dpi, telefono, activo) VALUES (?, ?, ?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getFecha_nacimiento());
            ps.setString(3, p.getDpi());
            ps.setString(4, p.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Paciente p) {
        String sql = "UPDATE paciente SET nombre=?, fecha_nacimiento=?, dpi=?, telefono=?, activo=? " +
                     "WHERE id_paciente=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getFecha_nacimiento());
            ps.setString(3, p.getDpi());
            ps.setString(4, p.getTelefono());
            ps.setBoolean(5, p.isActivo());
            ps.setInt(6, p.getId_paciente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE paciente SET activo = 0 WHERE id_paciente = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        return new Paciente(
            rs.getInt("id_paciente"),
            rs.getString("nombre"),
            rs.getString("fecha_nacimiento"),
            rs.getString("dpi"),
            rs.getString("telefono"),
            rs.getString("created_at"),
            rs.getBoolean("activo")
        );
    }
}
