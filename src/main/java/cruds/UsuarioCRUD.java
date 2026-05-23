package cruds;

import pojos.Rol;
import pojos.Usuario;
import triagesystem.Encriptador;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioCRUD {

    public Usuario login(String nombreUsuario, String clave) {
        String hash = Encriptador.sha256(clave);
        String sql  = "SELECT u.id_usuario, u.nombre_completo, u.nombre_usuario, u.clave_hash, " +
                      "       u.id_medico, u.creado_en, u.activo, " +
                      "       r.id_rol, r.nombre AS nombre_rol, r.descripcion, r.activo AS rol_activo " +
                      "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol " +
                      "WHERE u.nombre_usuario = ? AND u.clave_hash = ? AND u.activo = 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Usuario> getAll() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre_completo, u.nombre_usuario, u.clave_hash, " +
                     "       u.id_medico, u.creado_en, u.activo, " +
                     "       r.id_rol, r.nombre AS nombre_rol, r.descripcion, r.activo AS rol_activo " +
                     "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Integer insertGetId(Usuario u) {
        String sql = "INSERT INTO usuario (nombre_completo, nombre_usuario, clave_hash, id_rol, activo) " +
                     "VALUES (?, ?, ?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre_completo());
            ps.setString(2, u.getNombre_usuario());
            ps.setString(3, Encriptador.sha256(u.getClave_hash()));
            ps.setInt(4, u.getId_rol());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Usuario u) {
        return insertGetId(u) != null;
    }

    public boolean update(Usuario u) {
        String sql = "UPDATE usuario SET nombre_completo=?, nombre_usuario=?, id_rol=?, activo=? " +
                     "WHERE id_usuario=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre_completo());
            ps.setString(2, u.getNombre_usuario());
            ps.setInt(3, u.getId_rol());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5, u.getId_usuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean cambiarClave(int idUsuario, String nuevaClave) {
        String sql = "UPDATE usuario SET clave_hash=? WHERE id_usuario=?";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, Encriptador.sha256(nuevaClave));
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol(
            rs.getInt("id_rol"),
            rs.getString("nombre_rol"),
            rs.getString("descripcion"),
            rs.getBoolean("rol_activo")
        );
        Usuario u = new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre_completo"),
            rs.getString("nombre_usuario"),
            rs.getString("clave_hash"),
            rol,
            rs.getString("creado_en"),
            rs.getBoolean("activo")
        );
        int idMedico = rs.getInt("id_medico");
        if (!rs.wasNull()) u.setId_medico(idMedico);
        return u;
    }
}
