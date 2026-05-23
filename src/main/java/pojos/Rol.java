package pojos;

public class Rol {
    private Integer id_rol;
    private String  nombre;
    private String  descripcion;
    private boolean activo;

    public Rol(Integer id_rol, String nombre, String descripcion, boolean activo) {
        this.id_rol      = id_rol;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.activo      = activo;
    }

    public Integer getId_rol()      { return id_rol; }
    public String  getNombre()      { return nombre; }
    public String  getDescripcion() { return descripcion; }
    public boolean isActivo()       { return activo; }

    public void setId_rol(Integer id_rol)        { this.id_rol      = id_rol; }
    public void setNombre(String nombre)         { this.nombre      = nombre; }
    public void setDescripcion(String desc)      { this.descripcion = desc; }
    public void setActivo(boolean activo)        { this.activo      = activo; }
}
