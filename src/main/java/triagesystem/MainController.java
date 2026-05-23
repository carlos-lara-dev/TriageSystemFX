package triagesystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRol;

    @FXML private Button btnInicio;
    @FXML private Button btnCola;
    @FXML private Button btnConsultas;
    @FXML private Button btnPacientes;
    @FXML private Button btnReportes;
    @FXML private Button btnAdmin;

    private Button btnActivo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Sesion.estaActiva()) {
            lblNombreUsuario.setText(Sesion.getUsuarioActual().getNombre_completo());
            lblRol.setText(Sesion.getUsuarioActual().getNombre_rol());
        }
        aplicarVisibilidadPorRol();
        irInicio();
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
