package pojos;

public class Consulta {
    private Integer id_consulta;
    private Integer id_ingreso;
    private Integer id_medico;
    private String  hora_inicio;
    private String  hora_fin;
    private String  observaciones;
    private boolean activo;

    public Consulta(Integer id_consulta, Integer id_ingreso, Integer id_medico,
                    String hora_inicio, String hora_fin, String observaciones, boolean activo) {
        this.id_consulta   = id_consulta;
        this.id_ingreso    = id_ingreso;
        this.id_medico     = id_medico;
        this.hora_inicio   = hora_inicio;
        this.hora_fin      = hora_fin;
        this.observaciones = observaciones;
        this.activo        = activo;
    }

    public Consulta() {}

    public Integer getId_consulta()   { return id_consulta; }
    public Integer getId_ingreso()    { return id_ingreso; }
    public Integer getId_medico()     { return id_medico; }
    public String  getHora_inicio()   { return hora_inicio; }
    public String  getHora_fin()      { return hora_fin; }
    public String  getObservaciones() { return observaciones; }
    public boolean isActivo()         { return activo; }

    public void setId_consulta(Integer id_consulta)     { this.id_consulta   = id_consulta; }
    public void setId_ingreso(Integer id_ingreso)       { this.id_ingreso    = id_ingreso; }
    public void setId_medico(Integer id_medico)         { this.id_medico     = id_medico; }
    public void setHora_inicio(String hora_inicio)      { this.hora_inicio   = hora_inicio; }
    public void setHora_fin(String hora_fin)            { this.hora_fin      = hora_fin; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }
    public void setActivo(boolean activo)               { this.activo        = activo; }
}
