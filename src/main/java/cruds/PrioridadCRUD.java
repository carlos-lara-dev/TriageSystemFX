package cruds;

import pojos.Prioridad;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrioridadCRUD {

    public List<Prioridad> getAll() {
        List<Prioridad> lista = new ArrayList<>();
        String sql = "SELECT id_prioridad, valor, nombre, activo FROM prioridad WHERE activo = 1 ORDER BY valor DESC";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Prioridad getById(int id) {
        String sql = "SELECT id_prioridad, valor, nombre, activo FROM prioridad WHERE id_prioridad = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Prioridad p) {
        String sql = "INSERT INTO prioridad (valor, nombre, activo) VALUES (?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getValor());
            ps.setString(2, p.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Prioridad p) {
        String sql = "UPDATE prioridad SET valor=?, nombre=?, activo=? WHERE id_prioridad=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getValor());
            ps.setString(2, p.getNombre());
            ps.setBoolean(3, p.isActivo());
            ps.setInt(4, p.getId_prioridad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE prioridad SET activo = 0 WHERE id_prioridad = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Prioridad mapear(ResultSet rs) throws SQLException {
        return new Prioridad(
            rs.getInt("id_prioridad"),
            rs.getInt("valor"),
            rs.getString("nombre"),
            rs.getBoolean("activo")
        );
    }
}
