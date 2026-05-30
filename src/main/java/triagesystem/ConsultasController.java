package triagesystem;

import cruds.ConsultaCRUD;
import cruds.EstadoCRUD;
import cruds.IngresoCRUD;
import cruds.UsuarioMedicoCRUD;
import pojos.Consulta;
import pojos.Estado;
import pojos.IngresoDetalle;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ConsultasController implements Initializable {

    @FXML private TableView<IngresoDetalle>           tablaCola;
    @FXML private TableColumn<IngresoDetalle, String> ccColPos;
    @FXML private TableColumn<IngresoDetalle, String> ccColNombre;
    @FXML private TableColumn<IngresoDetalle, String> ccColPrioridad;
    @FXML private TableColumn<IngresoDetalle, String> ccColSintomas;
    @FXML private TableColumn<IngresoDetalle, String> ccColEspera;

    @FXML private VBox  panelSinSeleccion;
    @FXML private VBox  panelDetalle;

    @FXML private Label    lblNombrePaciente;
    @FXML private Label    lblDpi;
    @FXML private Label    lblPrioridad;
    @FXML private Label    lblSintomas;
    @FXML private TextArea txtObservaciones;
    @FXML private Button   btnAccion;

    private final IngresoCRUD       ingresoCRUD       = new IngresoCRUD();
    private final ConsultaCRUD      consultaCRUD      = new ConsultaCRUD();
    private final EstadoCRUD        estadoCRUD        = new EstadoCRUD();
    private final UsuarioMedicoCRUD usuarioMedicoCRUD = new UsuarioMedicoCRUD();

    private IngresoDetalle seleccionado;
    private Consulta       consultaActiva;
    private boolean        consultaEnCurso = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();

        tablaCola.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, nuevo) -> {
                if (!consultaEnCurso && nuevo != null) mostrarDetalle(nuevo);
            });

        mostrarEstado(false, false);
        javafx.application.Platform.runLater(() -> {
            cargarCola();
            restaurarConsultaActiva();
        });
    }

    /** Si el médico ya tenía una consulta en curso al iniciar sesión, restaura el Estado C. */
    private void restaurarConsultaActiva() {
        Integer idMedico = usuarioMedicoCRUD.getIdMedicoPorUsuario(
                               Sesion.getUsuarioActual().getId_usuario());
        if (idMedico == null) return;

        LoaderOverlay.runAsync(tablaCola,
            () -> {
                Consulta activa = consultaCRUD.getConsultaActivaPorMedico(idMedico);
                if (activa == null) return null;
                IngresoDetalle detalle = ingresoCRUD.getAllDetalle().stream()
                    .filter(d -> d.getId_ingreso().equals(activa.getId_ingreso()))
                    .findFirst().orElse(null);
                return new Object[]{ activa, detalle };
            },
            result -> {
                if (result == null) return;
                Consulta       activa  = (Consulta)       result[0];
                IngresoDetalle detalle = (IngresoDetalle) result[1];
                if (detalle == null) return;
                consultaActiva = activa;
                lblNombrePaciente.setText(detalle.getNombre_paciente());
                lblDpi.setText("DPI: " + detalle.getDpi());
                lblPrioridad.setText("Prioridad: " + detalle.getNombre_prioridad());
                lblSintomas.setText(detalle.getSintomas() != null ? detalle.getSintomas() : "—");
                txtObservaciones.setText(activa.getObservaciones() != null ? activa.getObservaciones() : "");
                seleccionado = detalle;
                mostrarEstado(true, true);
            }
        );
    }

    private void configurarColumnas() {
        tablaCola.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
            @Override protected void updateItem(IngresoDetalle item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-espera","row-consulta","row-atendido","row-cancelado");
                if (!empty && item != null && item.getId_estado() != null) {
                    switch (item.getId_estado()) {
                        case 1 -> getStyleClass().add("row-espera");
                        case 2 -> getStyleClass().add("row-consulta");
                        case 3 -> getStyleClass().add("row-atendido");
                        case 4 -> getStyleClass().add("row-cancelado");
                    }
                }
            }
        });
        ccColNombre.setCellValueFactory(new PropertyValueFactory<>("nombre_paciente"));
        ccColPrioridad.setCellValueFactory(new PropertyValueFactory<>("nombre_prioridad"));
        ccColSintomas.setCellValueFactory(new PropertyValueFactory<>("sintomas"));

        ccColPos.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        ccColEspera.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        ccColEspera.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try {
                    LocalDateTime ingreso = LocalDateTime.parse(
                        item, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    long mins = Duration.between(ingreso, LocalDateTime.now()).toMinutes();
                    setText(mins + " min");
                } catch (Exception e) { setText(item); }
            }
        });
    }

    @FXML
    public void cargarCola() {
        final Integer idMedico = usuarioMedicoCRUD.getIdMedicoPorUsuario(
                                     Sesion.getUsuarioActual().getId_usuario());
        LoaderOverlay.runAsync(tablaCola,
            () -> ingresoCRUD.getAllDetalle().stream()
                .filter(i -> {
                    int estado = i.getId_estado() == null ? 0 : i.getId_estado();
                    if (estado == 1) return true;
                    if (estado == 2) return idMedico != null && idMedico.equals(i.getIdMedicoConsulta());
                    return false;
                })
                .collect(Collectors.toList()),
            activos -> tablaCola.setItems(FXCollections.observableArrayList(activos))
        );
    }

    private void mostrarDetalle(IngresoDetalle d) {
        seleccionado = d;
        lblNombrePaciente.setText(d.getNombre_paciente());
        lblDpi.setText("DPI: " + d.getDpi());
        lblPrioridad.setText("Prioridad: " + d.getNombre_prioridad());
        lblSintomas.setText(d.getSintomas() != null ? d.getSintomas() : "—");
        txtObservaciones.clear();
        mostrarEstado(true, false);
    }

    private void mostrarEstado(boolean detalle, boolean enCurso) {
        panelSinSeleccion.setVisible(!detalle);
        panelSinSeleccion.setManaged(!detalle);
        panelDetalle.setVisible(detalle);
        panelDetalle.setManaged(detalle);

        txtObservaciones.setDisable(!enCurso);
        btnAccion.setText(enCurso ? "✔  Finalizar consulta" : "▶  Iniciar consulta");
        btnAccion.getStyleClass().removeAll("btn-primary", "btn-success");
        btnAccion.getStyleClass().add(enCurso ? "btn-success" : "btn-primary");
        consultaEnCurso = enCurso;
    }

    @FXML
    private void handleAccion() {
        if (!consultaEnCurso) iniciarConsulta();
        else                   finalizarConsulta();
    }

    private void iniciarConsulta() {
        if (seleccionado == null) return;

        Integer idMedico = usuarioMedicoCRUD.getIdMedicoPorUsuario(
                               Sesion.getUsuarioActual().getId_usuario());
        if (idMedico == null) {
            alerta("Sin médico asignado", "Tu usuario no tiene un médico vinculado en el sistema.");
            return;
        }

        final IngresoDetalle paciente = seleccionado;
        final int idMed = idMedico;

        LoaderOverlay.runAsync(tablaCola,
            () -> {
                // Verificar que el ingreso sigue en espera
                List<IngresoDetalle> actual = ingresoCRUD.getAllDetalle().stream()
                    .filter(i -> i.getId_ingreso().equals(paciente.getId_ingreso()))
                    .collect(Collectors.toList());
                if (actual.isEmpty() || actual.get(0).getId_estado() != 1)
                    return new Object[]{ "no_disponible", null };

                Consulta c = new Consulta();
                c.setId_ingreso(paciente.getId_ingreso());
                c.setId_medico(idMed);
                c.setHora_inicio(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                c.setHora_fin(null);
                c.setObservaciones("");
                if (!consultaCRUD.insert(c)) return new Object[]{ "error", null };
                ingresoCRUD.updateEstado(paciente.getId_ingreso(), 2);

                List<Consulta> consultas = consultaCRUD.getByIngreso(paciente.getId_ingreso());
                Consulta ultima = consultas.isEmpty() ? null : consultas.get(consultas.size() - 1);
                return new Object[]{ "ok", ultima };
            },
            result -> {
                String status = (String) result[0];
                switch (status) {
                    case "no_disponible" -> {
                        alerta("Paciente no disponible",
                               "Este paciente ya está siendo atendido por otro médico.\nLa lista ha sido actualizada.");
                        cargarCola();
                        mostrarEstado(false, false);
                        tablaCola.getSelectionModel().clearSelection();
                    }
                    case "error" -> alerta("Error", "No se pudo iniciar la consulta.");
                    case "ok" -> {
                        consultaActiva = (Consulta) result[1];
                        cargarCola();
                        mostrarEstado(true, true);
                    }
                }
            }
        );
    }

    private void finalizarConsulta() {
        if (consultaActiva == null) return;

        consultaActiva.setHora_fin(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        consultaActiva.setObservaciones(txtObservaciones.getText().trim());

        final Consulta consulta    = consultaActiva;
        final int      idIngreso   = seleccionado.getId_ingreso();

        LoaderOverlay.runAsync(tablaCola,
            () -> {
                if (!consultaCRUD.update(consulta)) return false;
                ingresoCRUD.updateEstado(idIngreso, 3);
                return true;
            },
            ok -> {
                if (!ok) { alerta("Error", "No se pudo finalizar la consulta."); return; }
                consultaActiva = null;
                seleccionado   = null;
                txtObservaciones.clear();
                cargarCola();
                mostrarEstado(false, false);
                tablaCola.getSelectionModel().clearSelection();
            }
        );
    }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
