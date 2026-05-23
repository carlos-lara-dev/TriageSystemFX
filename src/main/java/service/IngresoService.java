package service;

import cruds.ConsultaCRUD;
import cruds.IngresoCRUD;
import pojos.Consulta;
import pojos.Ingreso;
import pojos.IngresoDetalle;
import pojos.Medico;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class IngresoService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final IngresoCRUD  ingresoCRUD  = new IngresoCRUD();
    private final ConsultaCRUD consultaCRUD = new ConsultaCRUD();

    public boolean registrar(Ingreso ingreso) {
        return ingresoCRUD.insert(ingreso);
    }

    public List<IngresoDetalle> getColaActiva() {
        return ingresoCRUD.getAllDetalle().stream()
            .filter(i -> i.getId_estado() == 1 || i.getId_estado() == 2)
            .collect(Collectors.toList());
    }

    /**
     * Moves the next waiting patient to "En consulta" and creates a Consulta record.
     */
    public boolean atenderSiguiente(IngresoDetalle siguiente, Medico medico) {
        if (!ingresoCRUD.updateEstado(siguiente.getId_ingreso(), 2)) return false;

        Consulta c = new Consulta();
        c.setId_ingreso(siguiente.getId_ingreso());
        c.setId_medico(medico.getId_medico());
        c.setHora_inicio(LocalDateTime.now().format(TS));
        c.setHora_fin(null);
        c.setObservaciones("");
        consultaCRUD.insert(c);
        return true;
    }

    public boolean actualizarEstado(int idIngreso, int idEstado) {
        return ingresoCRUD.updateEstado(idIngreso, idEstado);
    }

    public boolean eliminar(int idIngreso) {
        return ingresoCRUD.delete(idIngreso);
    }
}
