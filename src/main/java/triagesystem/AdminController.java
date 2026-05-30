package triagesystem;

import cruds.*;
import pojos.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController implements Initializable {

    // ── Tab Usuarios ────────────────────────────────────────────────────────
    @FXML private TableView<Usuario>   tablaUsuarios;
    @FXML private TableColumn<Usuario, String>  uColNombre;
    @FXML private TableColumn<Usuario, String>  uColUsuario;
    @FXML private TableColumn<Usuario, String>  uColRol;
    @FXML private TableColumn<Usuario, Boolean> uColActivo;

    // ── Tab Médicos ─────────────────────────────────────────────────────────
    @FXML private TableView<Medico>    tablaMedicos;
    @FXML private TableColumn<Medico, String>  mColNombre;
    @FXML private TableColumn<Medico, Boolean> mColActivo;

    // ── Tab Catálogos ───────────────────────────────────────────────────────
    @FXML private TableView<Rol>       tablaRoles;
    @FXML private TableColumn<Rol, String> rColNombre;
    @FXML private TableColumn<Rol, String> rColDesc;
    @FXML private TableView<Prioridad>     tablaPrioridades;
    @FXML private TableColumn<Prioridad, String>  pColNombre;
    @FXML private TableColumn<Prioridad, Integer> pColValor;

    // ── CRUDs ───────────────────────────────────────────────────────────────
    private final UsuarioCRUD      usuarioCRUD      = new UsuarioCRUD();
    private final MedicoCRUD       medicoCRUD       = new MedicoCRUD();
    private final RolCRUD          rolCRUD          = new RolCRUD();
    private final PrioridadCRUD    prioridadCRUD    = new PrioridadCRUD();
    private final UsuarioMedicoCRUD usuarioMedicoCRUD = new UsuarioMedicoCRUD();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        javafx.application.Platform.runLater(this::cargarTodas);
    }

    private void configurarColumnas() {
        uColNombre.setCellValueFactory(new PropertyValueFactory<>("nombre_completo"));
        uColUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre_usuario"));
        uColRol.setCellValueFactory(new PropertyValueFactory<>("nombre_rol"));
        uColActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        uColActivo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : (v ? "Activo" : "Inactivo"));
            }
        });

        mColNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        mColActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        mColActivo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : (v ? "Activo" : "Inactivo"));
            }
        });

        rColNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        rColDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        pColNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        pColValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
    }

    private void cargarTodas() {
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> new Object[]{
                usuarioCRUD.getAll(),
                medicoCRUD.getAll(),
                rolCRUD.getAll(),
                prioridadCRUD.getAll()
            },
            result -> {
                @SuppressWarnings("unchecked") List<Usuario>   us = (List<Usuario>)   result[0];
                @SuppressWarnings("unchecked") List<Medico>    ms = (List<Medico>)    result[1];
                @SuppressWarnings("unchecked") List<Rol>       rs = (List<Rol>)       result[2];
                @SuppressWarnings("unchecked") List<Prioridad> ps = (List<Prioridad>) result[3];
                tablaUsuarios.setItems(FXCollections.observableArrayList(us));
                tablaMedicos.setItems(FXCollections.observableArrayList(ms));
                tablaRoles.setItems(FXCollections.observableArrayList(rs));
                tablaPrioridades.setItems(FXCollections.observableArrayList(ps));
            }
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB USUARIOS — roles que NO son Médico
    // ════════════════════════════════════════════════════════════════════════

    @FXML private void handleNuevoUsuario() {
        // Fetch roles en background, luego mostrar diálogo
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> rolesNoMedico(),
            roles -> {
                if (roles.isEmpty()) { alerta("Sin roles", "No hay roles disponibles para crear un usuario."); return; }
                Dialog<Usuario> dialog = crearDialogoUsuario(null, roles);
                dialog.showAndWait().ifPresent(u ->
                    LoaderOverlay.runAsync(tablaUsuarios,
                        () -> usuarioCRUD.insert(u),
                        ok -> {
                            if (ok) cargarTodas();
                            else alerta("Error", "No se pudo crear el usuario.");
                        }
                    )
                );
            }
        );
    }

    @FXML private void handleEditarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un usuario."); return; }

        final boolean esMedico = sel.getId_rol() != null && sel.getId_rol() == Roles.MEDICO;
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> esMedico ? rolCRUD.getAll() : rolesNoMedico(),
            roles -> {
                Dialog<Usuario> dialog = crearDialogoUsuario(sel, roles);
                dialog.showAndWait().ifPresent(u ->
                    LoaderOverlay.runAsync(tablaUsuarios,
                        () -> usuarioCRUD.update(u),
                        ok -> {
                            if (ok) cargarTodas();
                            else alerta("Error", "No se pudo actualizar el usuario.");
                        }
                    )
                );
            }
        );
    }

    @FXML private void handleDesactivarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un usuario."); return; }
        sel.setActivo(false);
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> usuarioCRUD.update(sel),
            ok -> { if (ok) cargarTodas(); }
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB MÉDICOS
    // ════════════════════════════════════════════════════════════════════════

    @FXML private void handleNuevoMedico() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Nuevo médico"); d.setHeaderText(null); d.setContentText("Nombre del médico:");
        d.showAndWait().ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                Medico m = new Medico(); m.setNombre(nombre.trim()); m.setActivo(true);
                LoaderOverlay.runAsync(tablaUsuarios,
                    () -> medicoCRUD.insert(m),
                    ok -> { if (ok) cargarTodas(); }
                );
            }
        });
    }

    @FXML private void handleEditarMedico() {
        Medico sel = tablaMedicos.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un médico."); return; }
        TextInputDialog d = new TextInputDialog(sel.getNombre());
        d.setTitle("Editar médico"); d.setHeaderText(null); d.setContentText("Nombre:");
        d.showAndWait().ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                sel.setNombre(nombre.trim());
                LoaderOverlay.runAsync(tablaUsuarios,
                    () -> medicoCRUD.update(sel),
                    ok -> { if (ok) cargarTodas(); }
                );
            }
        });
    }

    @FXML private void handleDesactivarMedico() {
        Medico sel = tablaMedicos.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un médico."); return; }
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> medicoCRUD.delete(sel.getId_medico()),
            ok -> { if (ok) cargarTodas(); }
        );
    }

    /**
     * Crea un usuario con rol Médico vinculado al médico seleccionado.
     * Solo es posible si el médico aún no tiene usuario asignado.
     */
    @FXML private void handleCrearUsuarioMedico() {
        Medico sel = tablaMedicos.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un médico de la tabla."); return; }

        // 1. Verificar + fetch rol Médico en background
        LoaderOverlay.runAsync(tablaUsuarios,
            () -> new Object[]{
                usuarioMedicoCRUD.existePorMedico(sel.getId_medico()),
                rolCRUD.getAll().stream().filter(r -> r.getId_rol() == Roles.MEDICO).findFirst().orElse(null)
            },
            result -> {
                boolean yaExiste = (boolean) result[0];
                Rol     rolMedico = (Rol)    result[1];

                if (yaExiste) {
                    alerta("Ya tiene usuario", "El médico \"" + sel.getNombre() + "\" ya tiene un usuario asignado.");
                    return;
                }
                if (rolMedico == null) {
                    alerta("Error", "No se encontró el rol 'Médico' en la base de datos.");
                    return;
                }

                // 2. Mostrar diálogo en hilo JavaFX
                Dialog<Usuario> dialog = crearDialogoUsuarioMedico(sel, rolMedico);
                dialog.showAndWait().ifPresent(u ->
                    // 3. Insertar en background
                    LoaderOverlay.runAsync(tablaUsuarios,
                        () -> {
                            Integer idUsuario = usuarioCRUD.insertGetId(u);
                            if (idUsuario == null) return "error";
                            if (!usuarioMedicoCRUD.insert(idUsuario, sel.getId_medico())) return "advertencia";
                            return "ok";
                        },
                        status -> {
                            if ("error".equals(status))       alerta("Error", "No se pudo crear el usuario.");
                            else if ("advertencia".equals(status)) alerta("Advertencia", "Usuario creado pero no se pudo vincular al médico.");
                            cargarTodas();
                        }
                    )
                );
            }
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // DIÁLOGOS
    // ════════════════════════════════════════════════════════════════════════

    /** Diálogo para crear o editar un usuario NO médico. */
    private Dialog<Usuario> crearDialogoUsuario(Usuario existente, List<Rol> roles) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(existente == null ? "Nuevo usuario" : "Editar usuario");

        TextField     fNombre  = new TextField(existente != null ? existente.getNombre_completo() : "");
        TextField     fUsuario = new TextField(existente != null ? existente.getNombre_usuario() : "");
        PasswordField fClave   = new PasswordField();
        ComboBox<Rol> cbRol    = new ComboBox<>(FXCollections.observableArrayList(roles));
        cbRol.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Rol r)   { return r != null ? r.getNombre() : ""; }
            @Override public Rol fromString(String s) { return null; }
        });

        // Pre-seleccionar rol actual al editar
        if (existente != null && existente.getId_rol() != null) {
            roles.stream()
                 .filter(r -> r.getId_rol().equals(existente.getId_rol()))
                 .findFirst()
                 .ifPresent(cbRol::setValue);
        }

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("Nombre completo:"), fNombre);
        grid.addRow(1, new Label("Nombre usuario:"),  fUsuario);
        grid.addRow(2, new Label("Contraseña:"),      fClave);
        grid.addRow(3, new Label("Rol:"),             cbRol);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Rol rolSeleccionado = cbRol.getValue();
                Usuario u = existente != null ? existente
                    : new Usuario(null, fNombre.getText().trim(), fUsuario.getText().trim(),
                                  fClave.getText(), rolSeleccionado, null, true);
                if (existente != null) {
                    u.setNombre_completo(fNombre.getText().trim());
                    u.setNombre_usuario(fUsuario.getText().trim());
                    if (!fClave.getText().isEmpty()) u.setClave_hash(fClave.getText());
                    if (rolSeleccionado != null) u.setRol(rolSeleccionado);
                }
                return u;
            }
            return null;
        });
        return dialog;
    }

    /** Diálogo para crear un usuario con rol Médico vinculado a un médico existente. */
    private Dialog<Usuario> crearDialogoUsuarioMedico(Medico medico, Rol rolMedico) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Crear usuario médico");
        dialog.setHeaderText("Médico: " + medico.getNombre());

        TextField     fNombre  = new TextField(medico.getNombre()); // nombre completo predeterminado
        TextField     fUsuario = new TextField();
        PasswordField fClave   = new PasswordField();

        Label lRol = new Label("Médico (asignado automáticamente)");
        lRol.setStyle("-fx-text-fill: #475569; -fx-font-style: italic;");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("Nombre completo:"), fNombre);
        grid.addRow(1, new Label("Nombre usuario:"),  fUsuario);
        grid.addRow(2, new Label("Contraseña:"),      fClave);
        grid.addRow(3, new Label("Rol:"),             lRol);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(400);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (fUsuario.getText().trim().isEmpty() || fClave.getText().isEmpty()) return null;
                return new Usuario(null, fNombre.getText().trim(), fUsuario.getText().trim(),
                                   fClave.getText(), rolMedico, null, true);
            }
            return null;
        });
        return dialog;
    }

    // ════════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ════════════════════════════════════════════════════════════════════════

    private List<Rol> rolesNoMedico() {
        return rolCRUD.getAll().stream()
            .filter(r -> r.getId_rol() != Roles.MEDICO)
            .collect(Collectors.toList());
    }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
