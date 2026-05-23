package pojos;

public class IngresoDetalle {
    private Integer id_ingreso;
    private Integer id_estado;
    private Integer id_medico_consulta;
    private String  nombre_paciente;
    private String  dpi;
    private String  nombre_prioridad;
    private String  nombre_estado;
    private String  nombre_medico;
    private String  sintomas;
    private String  created_at;

    public IngresoDetalle(Integer id_ingreso, Integer id_estado, Integer id_medico_consulta,
                          String nombre_paciente, String dpi, String nombre_prioridad,
                          String nombre_estado, String nombre_medico, String sintomas, String created_at) {
        this.id_ingreso          = id_ingreso;
        this.id_estado           = id_estado;
        this.id_medico_consulta  = id_medico_consulta;
        this.nombre_paciente     = nombre_paciente;
        this.dpi                 = dpi;
        this.nombre_prioridad    = nombre_prioridad;
        this.nombre_estado       = nombre_estado;
        this.nombre_medico       = nombre_medico;
        this.sintomas            = sintomas;
        this.created_at          = created_at;
    }

    public Integer getId_ingreso()         { return id_ingreso; }
    public Integer getId_estado()          { return id_estado; }
    public Integer getIdMedicoConsulta()   { return id_medico_consulta; }
    public String  getNombre_paciente()    { return nombre_paciente; }
    public String  getDpi()               { return dpi; }
    public String  getNombre_prioridad()  { return nombre_prioridad; }
    public String  getNombre_estado()     { return nombre_estado; }
    public String  getNombre_medico()     { return nombre_medico; }
    public String  getSintomas()          { return sintomas; }
    public String  getCreated_at()        { return created_at; }

    public void setId_ingreso(Integer id_ingreso)                  { this.id_ingreso         = id_ingreso; }
    public void setId_estado(Integer id_estado)                    { this.id_estado          = id_estado; }
    public void setIdMedicoConsulta(Integer id_medico_consulta)    { this.id_medico_consulta = id_medico_consulta; }
    public void setNombre_paciente(String nombre_paciente)         { this.nombre_paciente    = nombre_paciente; }
    public void setDpi(String dpi)                                 { this.dpi               = dpi; }
    public void setNombre_prioridad(String nombre_prioridad)       { this.nombre_prioridad   = nombre_prioridad; }
    public void setNombre_estado(String nombre_estado)             { this.nombre_estado      = nombre_estado; }
    public void setNombre_medico(String nombre_medico)             { this.nombre_medico      = nombre_medico; }
    public void setSintomas(String sintomas)                       { this.sintomas           = sintomas; }
    public void setCreated_at(String created_at)                   { this.created_at         = created_at; }
}
