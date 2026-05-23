package pojos;

public class Medico {
    private Integer id_medico;
    private String  nombre;
    private boolean activo;

    public Medico(Integer id_medico, String nombre, boolean activo) {
        this.id_medico = id_medico;
        this.nombre    = nombre;
        this.activo    = activo;
    }

    public Medico() {}

    public Integer getId_medico() { return id_medico; }
    public String  getNombre()    { return nombre; }
    public boolean isActivo()     { return activo; }

    public void setId_medico(Integer id_medico) { this.id_medico = id_medico; }
    public void setNombre(String nombre)        { this.nombre    = nombre; }
    public void setActivo(boolean activo)       { this.activo    = activo; }
}
