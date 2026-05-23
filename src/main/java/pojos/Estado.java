package pojos;

public class Estado {
    private Integer id_estado;
    private String  nombre;
    private boolean activo;

    public Estado(Integer id_estado, String nombre, boolean activo) {
        this.id_estado = id_estado;
        this.nombre    = nombre;
        this.activo    = activo;
    }

    public Estado() {}

    public Integer getId_estado() { return id_estado; }
    public String  getNombre()    { return nombre; }
    public boolean isActivo()     { return activo; }

    public void setId_estado(Integer id_estado) { this.id_estado = id_estado; }
    public void setNombre(String nombre)        { this.nombre    = nombre; }
    public void setActivo(boolean activo)       { this.activo    = activo; }
}
