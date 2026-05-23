package service;

import cruds.ConsultaCRUD;
import cruds.IngresoCRUD;
import cruds.UsuarioMedicoCRUD;
import pojos.Consulta;
import pojos.IngresoDetalle;
import triagesystem.Sesion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultaService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConsultaCRUD      consultaCRUD      = new ConsultaCRUD();
    private final IngresoCRUD       ingresoCRUD       = new IngresoCRUD();
    private final UsuarioMedicoCRUD usuarioMedicoCRUD = new UsuarioMedicoCRUD();

    public Integer getIdMedicoActual() {
        return usuarioMedicoCRUD.getIdMedicoPorUsuario(
            Sesion.getUsuarioActual().getId_usuario());
    }

    public Consulta getConsultaActiva(int idMedico) {
        return consultaCRUD.getConsultaActivaPorMedico(idMedico);
    }

    /**
     * Starts a consultation for the given ingreso.
     * Returns the created Consulta, or null if ingreso is no longer in "En espera" state.
     */
    public Consulta iniciar(IngresoDetalle ingreso, int idMedico) {
        List<IngresoDetalle> actual = ingresoCRUD.getAllDetalle().stream()
            .filter(i -> i.getId_ingreso().equals(ingreso.getId_ingreso()))
            .toList();
        if (actual.isEmpty() || actual.get(0).getId_estado() != 1) return null;

        Consulta c = new Consulta();
        c.setId_ingreso(ingreso.getId_ingreso());
        c.setId_medico(idMedico);
        c.setHora_inicio(LocalDateTime.now().format(TS));
        c.setHora_fin(null);
        c.setObservaciones("");

        if (!consultaCRUD.insert(c)) return null;
        ingresoCRUD.updateEstado(ingreso.getId_ingreso(), 2);

        List<Consulta> consultas = consultaCRUD.getByIngreso(ingreso.getId_ingreso());
        return consultas.isEmpty() ? null : consultas.get(consultas.size() - 1);
    }

    /**
     * Finalizes the active consultation.
     */
    public boolean finalizar(Consulta consulta, int idIngreso, String observaciones) {
        consulta.setHora_fin(LocalDateTime.now().format(TS));
        consulta.setObservaciones(observaciones);
        if (!consultaCRUD.update(consulta)) return false;
        ingresoCRUD.updateEstado(idIngreso, 3);
        return true;
    }
}
