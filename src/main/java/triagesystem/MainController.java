package triagesystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {

    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRol;

    @FXML private VBox   sidebar;
    @FXML private Button btnToggleSidebar;
    @FXML private Label  lblToggleIcon;

    @FXML private Button btnInicio;
    @FXML private Button btnCola;
    @FXML private Button btnConsultas;
    @FXML private Button btnPacientes;
    @FXML private Button btnReportes;
    @FXML private Button btnAdmin;

    @FXML private Label lblSeccion1;
    @FXML private Label lblSeccion2;
    @FXML private Label lblSeccion3;

    private Button  btnActivo;
    private boolean sidebarExpanded = true;
    private static final double SIDEBAR_W   = 185;
    private static final double COLLAPSED_W = 48;
    private static final int    ANIM_MS     = 220;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Sesion.estaActiva()) {
            lblNombreUsuario.setText(Sesion.getUsuarioActual().getNombre_completo());
            lblRol.setText(Sesion.getUsuarioActual().getNombre_rol());
        }
        aplicarVisibilidadPorRol();
        inicializarTextosNavBtn();
        irInicio();
    }

    /** Asigna los textos de los botones de navegación en modo expandido. */
    private void inicializarTextosNavBtn() {
        btnInicio.setText("  Inicio");
        btnConsultas.setText("  Mis Consultas");
        btnCola.setText("  Cola / Registro");
        btnPacientes.setText("  Pacientes");
        btnReportes.setText("  Reportería");
        btnAdmin.setText("  Administración");
    }

    // ── Toggle sidebar ─────────────────────────────────────────────────────

    @FXML
    private void toggleSidebar() {
        double fromW = sidebarExpanded ? SIDEBAR_W   : COLLAPSED_W;
        double toW   = sidebarExpanded ? COLLAPSED_W : SIDEBAR_W;

        Rectangle clip = new Rectangle(fromW, 4000);
        sidebar.setClip(clip);

        Timeline tl = new Timeline(new KeyFrame(
            Duration.millis(ANIM_MS),
            new KeyValue(clip.widthProperty(),        toW, Interpolator.EASE_BOTH),
            new KeyValue(sidebar.prefWidthProperty(), toW, Interpolator.EASE_BOTH),
            new KeyValue(sidebar.minWidthProperty(),  toW, Interpolator.EASE_BOTH),
            // maxWidth bloquea que el hover/relayout estire el sidebar
            new KeyValue(sidebar.maxWidthProperty(),  toW, Interpolator.EASE_BOTH)
        ));

        boolean collapsing = sidebarExpanded;
        if (collapsing) setNavTextsVisible(false);

        tl.setOnFinished(e -> {
            if (!collapsing) setNavTextsVisible(true);
            sidebar.setClip(null);
        });

        sidebarExpanded = !sidebarExpanded;
        lblToggleIcon.setText(sidebarExpanded ? "☰" : "▶");
        tl.play();
    }

    /** Muestra u oculta el texto y las etiquetas de sección del sidebar. */
    private void setNavTextsVisible(boolean visible) {
        // Padding del VBox: simétrico y ajustado cuando está contraído
        sidebar.setPadding(new javafx.geometry.Insets(16, visible ? 8 : 4, 16, visible ? 8 : 4));

        for (Button btn : navBtns()) {
            btn.setText(visible ? getNavText(btn) : "");
            btn.setAlignment(visible
                ? javafx.geometry.Pos.CENTER_LEFT
                : javafx.geometry.Pos.CENTER);
            // Sin padding horizontal propio: el VBox ya lo maneja
            btn.setPadding(new javafx.geometry.Insets(8, visible ? 6 : 0, 8, visible ? 6 : 0));
            // Fijar max-width para que el botón no intente crecer más allá del sidebar
            btn.setMaxWidth(visible ? Double.MAX_VALUE : COLLAPSED_W);
        }
        for (Node n : new Node[]{ lblSeccion1, lblSeccion2, lblSeccion3 }) {
            n.setVisible(visible);
            n.setManaged(visible);
        }
    }

    private Button[] navBtns() {
        return new Button[]{ btnInicio, btnConsultas, btnCola, btnPacientes, btnReportes, btnAdmin };
    }

    private String getNavText(Button btn) {
        if (btn == btnInicio)    return "  Inicio";
        if (btn == btnConsultas) return "  Mis Consultas";
        if (btn == btnCola)      return "  Cola / Registro";
        if (btn == btnPacientes) return "  Pacientes";
        if (btn == btnReportes)  return "  Reportería";
        if (btn == btnAdmin)     return "  Administración";
        return "";
    }

    private void aplicarVisibilidadPorRol() {
        boolean esAdmin         = Sesion.tieneRol(Roles.ADMINISTRADOR);
        boolean esRecepcionista = Sesion.tieneRol(Roles.RECEPCIONISTA);
        boolean esMedico        = Sesion.tieneRol(Roles.MEDICO);

        // Mis Consultas: solo Médico
        setBoton(btnConsultas, esMedico);

        // Cola / Registro y Pacientes: Admin y Recepcionista
        setBoton(btnCola,      esAdmin || esRecepcionista);
        setBoton(btnPacientes, esAdmin || esRecepcionista);

        // Reportería y Administración: solo Admin
        setBoton(btnReportes,  esAdmin);
        setBoton(btnAdmin,     esAdmin);
    }

    private void setBoton(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    @FXML public void irInicio()        { cargarVista("/vistas/dashboard.fxml",      btnInicio); }
    @FXML public void irCola()          { cargarVista("/vistas/cola_registro.fxml",  btnCola); }
    @FXML public void irMisConsultas()  { cargarVista("/vistas/consultas.fxml",      btnConsultas); }
    @FXML public void irPacientes()     { cargarVista("/vistas/pacientes.fxml",      btnPacientes); }
    @FXML public void irReportes()      { cargarVista("/vistas/reportes.fxml",       btnReportes); }
    @FXML public void irAdmin()         { cargarVista("/vistas/admin.fxml",          btnAdmin); }

    private void cargarVista(String fxml, Button boton) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(fxml));
            contenidoPrincipal.getChildren().setAll(vista);

            if (btnActivo != null) btnActivo.getStyleClass().remove("nav-btn-active");
            boton.getStyleClass().add("nav-btn-active");
            btnActivo = boton;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCerrarSesion() {
        Sesion.cerrar();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) contenidoPrincipal.getScene().getWindow();
            stage.setScene(new Scene(login));
            stage.setResizable(false);
            stage.setWidth(400);
            stage.setHeight(480);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
