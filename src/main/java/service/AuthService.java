package service;

import cruds.UsuarioCRUD;
import pojos.Usuario;
import triagesystem.Sesion;

public class AuthService {

    private final UsuarioCRUD usuarioCRUD = new UsuarioCRUD();

    public boolean login(String usuario, String clave) {
        Usuario u = usuarioCRUD.login(usuario, clave);
        if (u != null) {
            Sesion.iniciar(u);
            return true;
        }
        return false;
    }

    public void logout() {
        Sesion.cerrar();
    }
}
