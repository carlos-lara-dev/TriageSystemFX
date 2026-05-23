package pojos;

public class Usuario {
    private Integer id_usuario;
    private String  nombre_completo;
    private String  nombre_usuario;
    private String  clave_hash;
    private Rol     rol;
    private Integer id_medico;
    private String  creado_en;
    private boolean activo;

    public Usuario(Integer id_usuario, String nombre_completo, String nombre_usuario,
                   String clave_hash, Rol rol, String creado_en, boolean activo) {
        this.id_usuario      = id_usuario;
        this.nombre_completo = nombre_completo;
        this.nombre_usuario  = nombre_usuario;
        this.clave_hash      = clave_hash;
        this.rol             = rol;
        this.creado_en       = creado_en;
        this.activo          = activo;
    }

    public Usuario() {}

    public Integer getId_usuario()       { return id_usuario; }
    public String  getNombre_completo()  { return nombre_completo; }
    public String  getNombre_usuario()   { return nombre_usuario; }
    public String  getClave_hash()       { return clave_hash; }
    public Rol     getRol()              { return rol; }
    public Integer getId_medico()        { return id_medico; }
    public String  getCreado_en()        { return creado_en; }
    public boolean isActivo()            { return activo; }

    public String  getNombre_rol()       { return rol != null ? rol.getNombre() : null; }
    public Integer getId_rol()           { return rol != null ? rol.getId_rol() : null; }

    public void setId_usuario(Integer id_usuario)          { this.id_usuario      = id_usuario; }
    public void setNombre_completo(String nombre_completo) { this.nombre_completo = nombre_completo; }
    public void setNombre_usuario(String nombre_usuario)   { this.nombre_usuario  = nombre_usuario; }
    public void setClave_hash(String clave_hash)           { this.clave_hash      = clave_hash; }
    public void setRol(Rol rol)                            { this.rol             = rol; }
    public void setId_medico(Integer id_medico)            { this.id_medico       = id_medico; }
    public void setCreado_en(String creado_en)             { this.creado_en       = creado_en; }
    public void setActivo(boolean activo)                  { this.activo          = activo; }
}
