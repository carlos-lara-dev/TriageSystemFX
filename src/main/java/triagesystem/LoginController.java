package triagesystem;

import service.AuthService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML private TextField     txtNombreUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label         lblError;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void handleLogin() {
        String usuario = txtNombreUsuario.getText().trim();
        String clave   = txtClave.getText().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarError("Completa el usuario y la contraseña.");
            return;
        }

        // Mostrar retroalimentación mientras se consulta la BD
        lblError.setText("Verificando...");
        lblError.setStyle("-fx-text-fill: #64748b;");
        lblError.setVisible(true);

        LoaderOverlay.runAsync(txtNombreUsuario,
            () -> authService.login(usuario, clave),
            ok -> {
                if (!ok) {
                    mostrarError("Usuario o contraseña incorrectos.");
                    return;
                }
                abrirVentanaPrincipal();
            }
        );
    }

    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtNombreUsuario.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la ventana principal: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}
