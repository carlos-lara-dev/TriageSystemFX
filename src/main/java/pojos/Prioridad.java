package pojos;

public class Prioridad {
    private Integer id_prioridad;
    private Integer valor;
    private String  nombre;
    private boolean activo;

    public Prioridad(Integer id_prioridad, Integer valor, String nombre, boolean activo) {
        this.id_prioridad = id_prioridad;
        this.valor        = valor;
        this.nombre       = nombre;
        this.activo       = activo;
    }

    public Prioridad() {}

    public Integer getId_prioridad() { return id_prioridad; }
    public Integer getValor()        { return valor; }
    public String  getNombre()       { return nombre; }
    public boolean isActivo()        { return activo; }

    public void setId_prioridad(Integer id_prioridad) { this.id_prioridad = id_prioridad; }
    public void setValor(Integer valor)               { this.valor        = valor; }
    public void setNombre(String nombre)              { this.nombre       = nombre; }
    public void setActivo(boolean activo)             { this.activo       = activo; }
}
