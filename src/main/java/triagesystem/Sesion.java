package triagesystem;

import pojos.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {}

    public static Usuario getUsuarioActual()      { return usuarioActual; }
    public static void iniciar(Usuario u)         { usuarioActual = u; }
    public static void cerrar()                   { usuarioActual = null; }
    public static boolean estaActiva()            { return usuarioActual != null; }

    public static boolean tieneRol(int idRol) {
        return estaActiva()
            && usuarioActual.getId_rol() != null
            && usuarioActual.getId_rol() == idRol;
    }

    /** @deprecated Use {@link #tieneRol(int)} with {@link Roles} constants. */
    @Deprecated
    public static boolean tieneRol(String nombreRol) {
        return estaActiva()
            && usuarioActual.getRol() != null
            && usuarioActual.getRol().getNombre().equalsIgnoreCase(nombreRol);
    }
}
