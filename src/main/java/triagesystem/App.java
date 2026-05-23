package triagesystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Sistema de Consultas — UMG");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alerta.setTitle("Error al iniciar");
            alerta.setHeaderText("No se pudo cargar la pantalla de login");
            alerta.setContentText(e.getMessage() != null ? e.getMessage() : e.getClass().getName());
            alerta.showAndWait();
        }
    }
}
