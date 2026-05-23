package pojos;

public class Ingreso {
    private Integer id_ingreso;
    private Integer id_paciente;
    private Integer id_prioridad;
    private Integer id_estado;
    private String  sintomas;
    private String  created_at;
    private boolean activo;

    public Ingreso(Integer id_ingreso, Integer id_paciente, Integer id_prioridad,
                   Integer id_estado, String sintomas, String created_at, boolean activo) {
        this.id_ingreso   = id_ingreso;
        this.id_paciente  = id_paciente;
        this.id_prioridad = id_prioridad;
        this.id_estado    = id_estado;
        this.sintomas     = sintomas;
        this.created_at   = created_at;
        this.activo       = activo;
    }

    public Ingreso() {}

    public Integer getId_ingreso()   { return id_ingreso; }
    public Integer getId_paciente()  { return id_paciente; }
    public Integer getId_prioridad() { return id_prioridad; }
    public Integer getId_estado()    { return id_estado; }
    public String  getSintomas()     { return sintomas; }
    public String  getCreated_at()   { return created_at; }
    public boolean isActivo()        { return activo; }

    public void setId_ingreso(Integer id_ingreso)     { this.id_ingreso   = id_ingreso; }
    public void setId_paciente(Integer id_paciente)   { this.id_paciente  = id_paciente; }
    public void setId_prioridad(Integer id_prioridad) { this.id_prioridad = id_prioridad; }
    public void setId_estado(Integer id_estado)       { this.id_estado    = id_estado; }
    public void setSintomas(String sintomas)          { this.sintomas     = sintomas; }
    public void setCreated_at(String created_at)      { this.created_at   = created_at; }
    public void setActivo(boolean activo)             { this.activo       = activo; }
}
