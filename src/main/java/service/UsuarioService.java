package service;

import cruds.MedicoCRUD;
import cruds.RolCRUD;
import cruds.UsuarioCRUD;
import cruds.UsuarioMedicoCRUD;
import pojos.Medico;
import pojos.Rol;
import pojos.Usuario;
import triagesystem.Roles;

public class UsuarioService {

    private final UsuarioCRUD       usuarioCRUD       = new UsuarioCRUD();
    private final MedicoCRUD        medicoCRUD        = new MedicoCRUD();
    private final RolCRUD           rolCRUD           = new RolCRUD();
    private final UsuarioMedicoCRUD usuarioMedicoCRUD = new UsuarioMedicoCRUD();

    /**
     * Creates a doctor user linked to an existing Medico record.
     * Returns false if the medico already has a user or if creation fails.
     */
    public boolean crearUsuarioMedico(Medico medico, String nombreCompleto,
                                      String nombreUsuario, String clave) {
        if (usuarioMedicoCRUD.existePorMedico(medico.getId_medico())) return false;

        Rol rolMedico = rolCRUD.getById(Roles.MEDICO);
        if (rolMedico == null) return false;

        Usuario u = new Usuario(null, nombreCompleto, nombreUsuario, clave, rolMedico, null, true);
        Integer idUsuario = usuarioCRUD.insertGetId(u);
        if (idUsuario == null) return false;

        return usuarioMedicoCRUD.insert(idUsuario, medico.getId_medico());
    }
}
