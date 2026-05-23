package cruds;

import pojos.Estado;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoCRUD {

    public List<Estado> getAll() {
        List<Estado> lista = new ArrayList<>();
        String sql = "SELECT id_estado, nombre, activo FROM estado WHERE activo = 1 ORDER BY id_estado";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Estado getById(int id) {
        String sql = "SELECT id_estado, nombre, activo FROM estado WHERE id_estado = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Estado e) {
        String sql = "INSERT INTO estado (nombre, activo) VALUES (?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean update(Estado e) {
        String sql = "UPDATE estado SET nombre=?, activo=? WHERE id_estado=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setBoolean(2, e.isActivo());
            ps.setInt(3, e.getId_estado());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE estado SET activo = 0 WHERE id_estado = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    private Estado mapear(ResultSet rs) throws SQLException {
        return new Estado(
            rs.getInt("id_estado"),
            rs.getString("nombre"),
            rs.getBoolean("activo")
        );
    }
}
