package triagesystem;

import cruds.*;
import pojos.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ColaRegistroController implements Initializable {

    // ── Formulario de registro ──────────────────────────────────────────────
    @FXML private ComboBox<Paciente>  cbPaciente;
    @FXML private ComboBox<Prioridad> cbPrioridad;
    @FXML private ComboBox<Estado>    cbEstado;
    @FXML private ComboBox<Medico>    cbMedicoForm;
    @FXML private TextArea            txtSintomas;
    @FXML private Label               lblMensaje;

    // ── Cola de atención ────────────────────────────────────────────────────
    @FXML private Label                               lblSiguiente;
    @FXML private Label                               lblTotalCola;
    @FXML private TableView<IngresoDetalle>           tablaCola;
    @FXML private TableColumn<IngresoDetalle, String> colPos;
    @FXML private TableColumn<IngresoDetalle, String> colEstado;
    @FXML private TableColumn<IngresoDetalle, String> colNombre;
    @FXML private TableColumn<IngresoDetalle, String> colDpi;
    @FXML private TableColumn<IngresoDetalle, String> colPrioridad;
    @FXML private TableColumn<IngresoDetalle, String> colSintomas;
    @FXML private TableColumn<IngresoDetalle, String> colMedico;
    @FXML private TableColumn<IngresoDetalle, String> colIngreso;

    // ── CRUDs ───────────────────────────────────────────────────────────────
    private final PacienteCRUD  pacienteCRUD  = new PacienteCRUD();
    private final PrioridadCRUD prioridadCRUD = new PrioridadCRUD();
    private final EstadoCRUD    estadoCRUD    = new EstadoCRUD();
    private final MedicoCRUD    medicoCRUD    = new MedicoCRUD();
    private final IngresoCRUD   ingresoCRUD   = new IngresoCRUD();
    private final ConsultaCRUD  consultaCRUD  = new ConsultaCRUD();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        javafx.application.Platform.runLater(() -> {
            cargarCombosFormulario();
            cargarTabla();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // FORMULARIO DE REGISTRO
    // ════════════════════════════════════════════════════════════════════════

    private void cargarCombosFormulario() {
        LoaderOverlay.runAsync(tablaCola,
            () -> new Object[]{
                pacienteCRUD.getAll(),
                prioridadCRUD.getAll(),
                estadoCRUD.getAll(),
                medicoCRUD.getAll()
            },
            result -> {
                @SuppressWarnings("unchecked") List<Paciente>  pacientes   = (List<Paciente>)  result[0];
                @SuppressWarnings("unchecked") List<Prioridad> prioridades = (List<Prioridad>) result[1];
                @SuppressWarnings("unchecked") List<Estado>    estados     = (List<Estado>)    result[2];
                @SuppressWarnings("unchecked") List<Medico>    medicos     = (List<Medico>)    result[3];

                cbPaciente.setItems(FXCollections.observableArrayList(pacientes));
                cbPaciente.setConverter(conv(p -> p != null ? p.getNombre() + " — " + p.getDpi() : ""));

                cbPrioridad.setItems(FXCollections.observableArrayList(prioridades));
                cbPrioridad.setConverter(conv(p -> p != null ? p.getNombre() : ""));

                cbEstado.setItems(FXCollections.observableArrayList(estados));
                cbEstado.setConverter(conv(e -> e != null ? e.getNombre() : ""));
                if (!estados.isEmpty()) cbEstado.setValue(estados.get(0));

                cbMedicoForm.setItems(FXCollections.observableArrayList(medicos));
                cbMedicoForm.setConverter(conv(m -> m != null ? m.getNombre() : ""));
            }
        );
    }

    @FXML
    private void handleRegistrar() {
        Paciente  paciente  = cbPaciente.getValue();
        Prioridad prioridad = cbPrioridad.getValue();
        Estado    estado    = cbEstado.getValue();
        String    sintomas  = txtSintomas.getText().trim();

        if (paciente == null || prioridad == null || estado == null || sintomas.isEmpty()) {
            mostrarMensaje("Completa todos los campos obligatorios.", false);
            return;
        }

        Ingreso ingreso = new Ingreso();
        ingreso.setId_paciente(paciente.getId_paciente());
        ingreso.setId_prioridad(prioridad.getId_prioridad());
        ingreso.setId_estado(estado.getId_estado());
        ingreso.setSintomas(sintomas);

        final Medico medico = cbMedicoForm.getValue();
        final String dpiPaciente = paciente.getDpi();

        LoaderOverlay.runAsync(tablaCola,
            () -> {
                if (!ingresoCRUD.insert(ingreso)) return false;
                if (medico != null) {
                    List<IngresoDetalle> detalles = ingresoCRUD.getAllDetalle();
                    IngresoDetalle ultimo = detalles.stream()
                        .filter(d -> d.getDpi().equals(dpiPaciente))
                        .findFirst().orElse(null);
                    if (ultimo != null) {
                        Consulta c = new Consulta();
                        c.setId_ingreso(ultimo.getId_ingreso());
                        c.setId_medico(medico.getId_medico());
                        c.setHora_inicio(java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        c.setHora_fin("");
                        c.setObservaciones("");
                        consultaCRUD.insert(c);
                    }
                }
                return true;
            },
            ok -> {
                if (ok) {
                    mostrarMensaje("Ingreso registrado correctamente.", true);
                    handleLimpiar();
                    cargarTabla();
                } else {
                    mostrarMensaje("No se pudo registrar el ingreso.", false);
                }
            }
        );
    }

    @FXML
    private void handleLimpiar() {
        cbPaciente.setValue(null);
        cbPrioridad.setValue(null);
        cbMedicoForm.setValue(null);
        txtSintomas.clear();
        List<Estado> estados = estadoCRUD.getAll();
        if (!estados.isEmpty()) cbEstado.setValue(estados.get(0));
        lblMensaje.setVisible(false);
    }

    private void mostrarMensaje(String msg, boolean exito) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle(exito ? "-fx-text-fill: #16a34a; -fx-font-size: 12px;"
                                  : "-fx-text-fill: #dc2626; -fx-font-size: 12px;");
        lblMensaje.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    // COLA DE ATENCIÓN
    // ════════════════════════════════════════════════════════════════════════

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
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombre_estado"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre_paciente"));
        colDpi.setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("nombre_prioridad"));
        colSintomas.setCellValueFactory(new PropertyValueFactory<>("sintomas"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("nombre_medico"));
        colIngreso.setCellValueFactory(new PropertyValueFactory<>("created_at"));

        colPos.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colIngreso.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item.length() >= 16 ? item.substring(11, 16) : item);
            }
        });
    }

    @FXML
    public void cargarTabla() {
        LoaderOverlay.runAsync(tablaCola,
            () -> ingresoCRUD.getAllDetalle().stream()
                .filter(i -> i.getId_estado() == 1 || i.getId_estado() == 2)
                .collect(java.util.stream.Collectors.toList()),
            lista -> {
                tablaCola.setItems(FXCollections.observableArrayList(lista));
                lblTotalCola.setText("Cola (" + lista.size() + " pacientes)");
                IngresoDetalle siguiente = lista.stream()
                    .filter(i -> i.getId_estado() == 1).findFirst().orElse(null);
                if (siguiente != null)
                    lblSiguiente.setText(siguiente.getNombre_paciente() + " — " + siguiente.getNombre_prioridad());
                else
                    lblSiguiente.setText("Sin pacientes en espera");
            }
        );
    }

    @FXML
    private void handleAtenderSiguiente() {
        if (tablaCola.getItems().isEmpty()) {
            alerta("Cola vacía", "No hay pacientes en espera.");
            return;
        }

        // 1. Obtener médicos disponibles en background
        LoaderOverlay.runAsync(tablaCola,
            () -> medicoCRUD.getMedicosDisponibles(),
            disponibles -> {
                if (disponibles.isEmpty()) {
                    alerta("Sin médicos disponibles",
                           "No hay médicos disponibles en este momento.\nTodos están atendiendo consultas activas.");
                    return;
                }

                // 2. Mostrar diálogo en hilo JavaFX
                IngresoDetalle siguiente = tablaCola.getItems().get(0);
                Medico medicoElegido = mostrarDialogoMedico(siguiente.getNombre_paciente(), disponibles);
                if (medicoElegido == null) return;

                final Medico medicoFinal = medicoElegido;
                final int idIngreso = siguiente.getId_ingreso();

                // 3. Ejecutar DB en background
                LoaderOverlay.runAsync(tablaCola,
                    () -> {
                        if (!ingresoCRUD.updateEstado(idIngreso, 2)) return false;
                        Consulta c = new Consulta();
                        c.setId_ingreso(idIngreso);
                        c.setId_medico(medicoFinal.getId_medico());
                        c.setHora_inicio(java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        c.setHora_fin("");
                        c.setObservaciones("");
                        consultaCRUD.insert(c);
                        return true;
                    },
                    ok -> {
                        if (!ok) alerta("Error", "No se pudo actualizar el estado del paciente.");
                        cargarTabla();
                    }
                );
            }
        );
    }

    /** Muestra un diálogo con ComboBox para elegir el médico disponible. */
    private Medico mostrarDialogoMedico(String nombrePaciente, List<Medico> disponibles) {
        Dialog<Medico> dialog = new Dialog<>();
        dialog.setTitle("Asignar médico");
        dialog.setHeaderText("Paciente: " + nombrePaciente);

        ComboBox<Medico> cbDialog = new ComboBox<>(FXCollections.observableArrayList(disponibles));
        cbDialog.setConverter(conv(m -> m != null ? m.getNombre() : ""));
        cbDialog.setPromptText("Selecciona un médico disponible");
        cbDialog.setPrefWidth(260);
        if (!disponibles.isEmpty()) cbDialog.setValue(disponibles.get(0));

        javafx.scene.layout.VBox contenido = new javafx.scene.layout.VBox(8);
        contenido.getChildren().addAll(
            new Label("Médicos disponibles (" + disponibles.size() + "):"),
            cbDialog
        );
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().setPrefWidth(340);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> bt == ButtonType.OK ? cbDialog.getValue() : null);
        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private void handleActualizarEstado() {
        IngresoDetalle sel = tablaCola.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un paciente."); return; }

        // 1. Fetch estados en background
        LoaderOverlay.runAsync(tablaCola,
            () -> estadoCRUD.getAll(),
            estados -> {
                // 2. Mostrar diálogo en hilo JavaFX
                List<String> nombres = estados.stream().map(Estado::getNombre).collect(Collectors.toList());
                ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
                dialog.setTitle("Actualizar estado");
                dialog.setHeaderText("Paciente: " + sel.getNombre_paciente());
                dialog.setContentText("Nuevo estado:");
                dialog.showAndWait().ifPresent(elegido -> {
                    Estado e = estados.stream()
                        .filter(x -> x.getNombre().equals(elegido))
                        .findFirst().orElse(null);
                    if (e != null) {
                        final int idEstado = e.getId_estado();
                        // 3. Actualizar en background
                        LoaderOverlay.runAsync(tablaCola,
                            () -> ingresoCRUD.updateEstado(sel.getId_ingreso(), idEstado),
                            ok -> { if (ok) cargarTabla(); }
                        );
                    }
                });
            }
        );
    }

    @FXML
    private void handleEliminar() {
        IngresoDetalle sel = tablaCola.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un paciente."); return; }
        LoaderOverlay.runAsync(tablaCola,
            () -> ingresoCRUD.delete(sel.getId_ingreso()),
            ok -> {
                if (ok) cargarTabla();
                else alerta("Error", "No se pudo eliminar el ingreso.");
            }
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ════════════════════════════════════════════════════════════════════════

    private <T> StringConverter<T> conv(java.util.function.Function<T, String> fn) {
        return new StringConverter<>() {
            @Override public String toString(T o)   { return fn.apply(o); }
            @Override public T fromString(String s) { return null; }
        };
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
