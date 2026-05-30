package triagesystem;

import cruds.IngresoCRUD;
import cruds.ReporteCRUD;
import pojos.IngresoDetalle;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

public class DashboardController implements Initializable {

    @FXML private Label lblFecha;
    @FXML private Label lblEnEspera;
    @FXML private Label lblEnConsulta;
    @FXML private Label lblCriticos;
    @FXML private Label lblAtendidos;

    @FXML private TableView<IngresoDetalle>              tablaProximos;
    @FXML private TableColumn<IngresoDetalle, String>    colPos;
    @FXML private TableColumn<IngresoDetalle, String>    colNombre;
    @FXML private TableColumn<IngresoDetalle, String>    colPrioridad;
    @FXML private TableColumn<IngresoDetalle, String>    colEspera;
    @FXML private TableColumn<IngresoDetalle, String>    colEstado;

    private final IngresoCRUD  ingresoCRUD  = new IngresoCRUD();
    private final ReporteCRUD  reporteCRUD  = new ReporteCRUD();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblFecha.setText("Resumen del día — " +
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es"))));
        configurarColumnas();
        // Platform.runLater garantiza que el nodo ya está en la escena
        // y que LoaderOverlay puede encontrar el StackPane raíz
        javafx.application.Platform.runLater(this::cargarDatos);
    }

    private void configurarColumnas() {
        tablaProximos.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
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
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre_paciente"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("nombre_prioridad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombre_estado"));

        colPos.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        colEspera.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        colEspera.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try {
                    LocalDateTime ingreso = LocalDateTime.parse(
                        item, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    long minutos = Duration.between(ingreso, LocalDateTime.now()).toMinutes();
                    setText(minutos + " min");
                } catch (Exception e) {
                    setText(item);
                }
            }
        });
    }

    @FXML
    public void cargarDatos() {
        LoaderOverlay.runAsync(tablaProximos,
            () -> {
                List<IngresoDetalle> todos = ingresoCRUD.getAllDetalle();
                String hoy = LocalDate.now().toString();
                int atendidos = reporteCRUD.getTotalAtendidos(hoy, hoy);
                return new Object[]{ todos, atendidos };
            },
            result -> {
                @SuppressWarnings("unchecked")
                List<IngresoDetalle> todos = (List<IngresoDetalle>) result[0];
                int atendidos = (int) result[1];

                long enEspera   = todos.stream().filter(i -> i.getId_estado() == 1).count();
                long enConsulta = todos.stream().filter(i -> i.getId_estado() == 2).count();
                long criticos   = todos.stream().filter(i ->
                    i.getNombre_prioridad().equalsIgnoreCase("Critical")).count();

                lblEnEspera.setText(String.valueOf(enEspera));
                lblEnConsulta.setText(String.valueOf(enConsulta));
                lblCriticos.setText(String.valueOf(criticos));
                lblAtendidos.setText(String.valueOf(atendidos));

                List<IngresoDetalle> proximos = todos.stream()
                    .filter(i -> i.getId_estado() == 1)
                    .limit(5)
                    .collect(Collectors.toList());
                tablaProximos.setItems(FXCollections.observableArrayList(proximos));
            }
        );
    }

    @FXML
    private void irACola() {
        try {
            StackPane contenido = (StackPane) tablaProximos.getScene().lookup("#contenidoPrincipal");
            if (contenido != null) {
                javafx.scene.Node vista = FXMLLoader.load(getClass().getResource("/vistas/cola_registro.fxml"));
                contenido.getChildren().setAll(vista);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
