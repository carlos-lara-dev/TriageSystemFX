package pojos;

public class Paciente {
    private Integer id_paciente;
    private String  nombre;
    private String  fecha_nacimiento;
    private String  dpi;
    private String  telefono;
    private String  created_at;
    private boolean activo;

    public Paciente(Integer id_paciente, String nombre, String fecha_nacimiento,
                    String dpi, String telefono, String created_at, boolean activo) {
        this.id_paciente      = id_paciente;
        this.nombre           = nombre;
        this.fecha_nacimiento = fecha_nacimiento;
        this.dpi              = dpi;
        this.telefono         = telefono;
        this.created_at       = created_at;
        this.activo           = activo;
    }

    public Paciente() {}

    public Integer getId_paciente()      { return id_paciente; }
    public String  getNombre()           { return nombre; }
    public String  getFecha_nacimiento() { return fecha_nacimiento; }
    public String  getDpi()              { return dpi; }
    public String  getTelefono()         { return telefono; }
    public String  getCreated_at()       { return created_at; }
    public boolean isActivo()            { return activo; }

    public void setId_paciente(Integer id_paciente)           { this.id_paciente      = id_paciente; }
    public void setNombre(String nombre)                      { this.nombre           = nombre; }
    public void setFecha_nacimiento(String fecha_nacimiento)  { this.fecha_nacimiento = fecha_nacimiento; }
    public void setDpi(String dpi)                            { this.dpi              = dpi; }
    public void setTelefono(String telefono)                  { this.telefono         = telefono; }
    public void setCreated_at(String created_at)              { this.created_at       = created_at; }
    public void setActivo(boolean activo)                     { this.activo           = activo; }
}
