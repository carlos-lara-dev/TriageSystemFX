package cruds;

import pojos.UsuarioMedico;
import triagesystem.SqlConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioMedicoCRUD {

    public List<UsuarioMedico> getAll() {
        List<UsuarioMedico> lista = new ArrayList<>();
        String sql = "SELECT id_usuario_medico, id_usuario, id_medico, activo FROM usuario_medico WHERE activo = 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Integer getIdMedicoPorUsuario(int idUsuario) {
        String sql = "SELECT id_medico FROM usuario_medico WHERE id_usuario = ? AND activo = 1 LIMIT 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_medico");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean existePorMedico(int idMedico) {
        String sql = "SELECT 1 FROM usuario_medico WHERE id_medico = ? AND activo = 1 LIMIT 1";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean insert(int idUsuario, int idMedico) {
        String sql = "INSERT INTO usuario_medico (id_usuario, id_medico, activo) VALUES (?, ?, 1)";
        try (Connection c = SqlConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMedico);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private UsuarioMedico mapear(ResultSet rs) throws SQLException {
        return new UsuarioMedico(
            rs.getInt("id_usuario_medico"),
            rs.getInt("id_usuario"),
            rs.getInt("id_medico"),
            rs.getBoolean("activo")
        );
    }
}
