package triagesystem;

import cruds.PacienteCRUD;
import pojos.Paciente;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class PacientesController implements Initializable {

    @FXML private TextField             txtBuscar;
    @FXML private TableView<Paciente>   tablaPacientes;
    @FXML private TableColumn<Paciente, String>  colNombre;
    @FXML private TableColumn<Paciente, String>  colDpi;
    @FXML private TableColumn<Paciente, String>  colTelefono;
    @FXML private TableColumn<Paciente, String>  colFecha;
    @FXML private TableColumn<Paciente, Boolean> colEstado;

    @FXML private VBox       panelFormulario;
    @FXML private Label      lblTituloForm;
    @FXML private TextField  fNombre;
    @FXML private TextField  fDpi;
    @FXML private TextField  fTelefono;
    @FXML private DatePicker fFecha;

    private final PacienteCRUD pacienteCRUD = new PacienteCRUD();
    private Paciente pacienteEditando = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        javafx.application.Platform.runLater(this::cargarTabla);
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDpi.setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha_nacimiento"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Activo" : "Inactivo"));
            }
        });
    }

    private void cargarTabla() {
        LoaderOverlay.runAsync(tablaPacientes,
            () -> pacienteCRUD.getAll(),
            lista -> tablaPacientes.setItems(FXCollections.observableArrayList(lista))
        );
    }

    @FXML private void handleBuscar() {
        String termino = txtBuscar.getText().trim().toLowerCase();
        LoaderOverlay.runAsync(tablaPacientes,
            () -> {
                List<Paciente> todos = pacienteCRUD.getAll();
                if (!termino.isEmpty()) {
                    return todos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(termino)
                                  || p.getDpi().toLowerCase().contains(termino))
                        .collect(java.util.stream.Collectors.toList());
                }
                return todos;
            },
            lista -> tablaPacientes.setItems(FXCollections.observableArrayList(lista))
        );
    }

    @FXML private void handleNuevo() {
        pacienteEditando = null;
        limpiarFormulario();
        lblTituloForm.setText("Nuevo paciente");
        mostrarFormulario(true);
    }

    @FXML private void handleEditar() {
        Paciente sel = tablaPacientes.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un paciente para editar."); return; }
        pacienteEditando = sel;
        fNombre.setText(sel.getNombre());
        fDpi.setText(sel.getDpi());
        fTelefono.setText(sel.getTelefono() != null ? sel.getTelefono() : "");
        if (sel.getFecha_nacimiento() != null)
            fFecha.setValue(java.time.LocalDate.parse(sel.getFecha_nacimiento()));
        lblTituloForm.setText("Editar paciente");
        mostrarFormulario(true);
    }

    @FXML private void handleGuardar() {
        String nombre = fNombre.getText().trim();
        String dpi    = fDpi.getText().trim();
        if (nombre.isEmpty() || dpi.isEmpty() || fFecha.getValue() == null) {
            alerta("Campos requeridos", "Completa nombre, DPI y fecha de nacimiento.");
            return;
        }
        Paciente p = pacienteEditando != null ? pacienteEditando : new Paciente();
        p.setNombre(nombre);
        p.setDpi(dpi);
        p.setTelefono(fTelefono.getText().trim());
        p.setFecha_nacimiento(fFecha.getValue().toString());

        final boolean esEdicion = pacienteEditando != null;
        LoaderOverlay.runAsync(tablaPacientes,
            () -> esEdicion ? pacienteCRUD.update(p) : pacienteCRUD.insert(p),
            ok -> {
                if (ok) { mostrarFormulario(false); cargarTabla(); }
                else alerta("Error", "No se pudo guardar el paciente.");
            }
        );
    }

    @FXML private void handleDesactivar() {
        Paciente sel = tablaPacientes.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un paciente."); return; }
        LoaderOverlay.runAsync(tablaPacientes,
            () -> pacienteCRUD.delete(sel.getId_paciente()),
            ok -> {
                if (ok) cargarTabla();
                else alerta("Error", "No se pudo desactivar el paciente.");
            }
        );
    }

    @FXML private void handleCancelar() { mostrarFormulario(false); }

    private void mostrarFormulario(boolean visible) {
        panelFormulario.setVisible(visible);
        panelFormulario.setManaged(visible);
    }

    private void limpiarFormulario() { fNombre.clear(); fDpi.clear(); fTelefono.clear(); fFecha.setValue(null); }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
