package cruds;

import pojos.Rol;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolCRUD {

    public List<Rol> getAll() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre, descripcion, activo FROM rol WHERE activo = 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Rol getById(int id) {
        String sql = "SELECT id_rol, nombre, descripcion, activo FROM rol WHERE id_rol = ?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Rol mapear(ResultSet rs) throws SQLException {
        return new Rol(
            rs.getInt("id_rol"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBoolean("activo")
        );
    }
}
