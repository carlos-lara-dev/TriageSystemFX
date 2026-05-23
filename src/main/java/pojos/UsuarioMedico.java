package pojos;

public class UsuarioMedico {
    private Integer id_usuario_medico;
    private Integer id_usuario;
    private Integer id_medico;
    private boolean activo;

    public UsuarioMedico(Integer id_usuario_medico, Integer id_usuario, Integer id_medico, boolean activo) {
        this.id_usuario_medico = id_usuario_medico;
        this.id_usuario        = id_usuario;
        this.id_medico         = id_medico;
        this.activo            = activo;
    }

    public UsuarioMedico() {}

    public Integer getId_usuario_medico() { return id_usuario_medico; }
    public Integer getId_usuario()        { return id_usuario; }
    public Integer getId_medico()         { return id_medico; }
    public boolean isActivo()             { return activo; }

    public void setId_usuario_medico(Integer id_usuario_medico) { this.id_usuario_medico = id_usuario_medico; }
    public void setId_usuario(Integer id_usuario)               { this.id_usuario        = id_usuario; }
    public void setId_medico(Integer id_medico)                 { this.id_medico         = id_medico; }
    public void setActivo(boolean activo)                       { this.activo            = activo; }
}
