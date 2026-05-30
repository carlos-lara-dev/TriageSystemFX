package triagesystem;

import cruds.ReporteCRUD;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.Node;

public class ReportesController implements Initializable {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    @FXML private Label lblEspera;
    @FXML private Label lblConsulta;
    @FXML private Label lblAtendidos;

    @FXML private BarChart<String, Number> graficaHoras;
    @FXML private BarChart<Number, String> graficaMedicos;
    @FXML private PieChart                 graficaPrioridades;

    private final ReporteCRUD reporteCRUD = new ReporteCRUD();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        graficaHoras.getStyleClass().add("chart-horas");
        graficaMedicos.getStyleClass().add("chart-medicos");
        javafx.application.Platform.runLater(this::filtrarHoy);
    }

    @FXML public void filtrarHoy() {
        dpDesde.setValue(LocalDate.now());
        dpHasta.setValue(LocalDate.now());
        cargarDatos(LocalDate.now().toString(), LocalDate.now().toString());
    }

    @FXML public void filtrarSemana() {
        LocalDate desde = LocalDate.now().minusDays(6);
        LocalDate hasta = LocalDate.now();
        dpDesde.setValue(desde);
        dpHasta.setValue(hasta);
        cargarDatos(desde.toString(), hasta.toString());
    }

    @FXML public void filtrarMes() {
        LocalDate desde = LocalDate.now().withDayOfMonth(1);
        LocalDate hasta = LocalDate.now();
        dpDesde.setValue(desde);
        dpHasta.setValue(hasta);
        cargarDatos(desde.toString(), hasta.toString());
    }

    @FXML public void handleFiltrar() {
        if (dpDesde.getValue() == null || dpHasta.getValue() == null) return;
        cargarDatos(dpDesde.getValue().toString(), dpHasta.getValue().toString());
    }

    private void cargarDatos(String desde, String hasta) {
        // Usar graficaHoras como nodo de referencia para el overlay
        Node refNode = graficaHoras;
        LoaderOverlay.runAsync(refNode,
            () -> {
                double               espera      = reporteCRUD.getPromedioEsperaMinutos(desde, hasta);
                double               consulta    = reporteCRUD.getPromedioConsultaMinutos(desde, hasta);
                int                  atend       = reporteCRUD.getTotalAtendidos(desde, hasta);
                Map<Integer,Integer> horas       = reporteCRUD.getIngresosPorHora(desde, hasta);
                Map<String,Integer>  medicos     = reporteCRUD.getTopMedicos(desde, hasta);
                Map<String,Integer>  prioridades = reporteCRUD.getIngresosPorPrioridad(desde, hasta);
                return new Object[]{ espera, consulta, atend, horas, medicos, prioridades };
            },
            result -> {
                double espera   = (double) result[0];
                double consulta = (double) result[1];
                int    atend    = (int)    result[2];
                @SuppressWarnings("unchecked") Map<Integer,Integer> horas       = (Map<Integer,Integer>) result[3];
                @SuppressWarnings("unchecked") Map<String,Integer>  medicos     = (Map<String,Integer>)  result[4];
                @SuppressWarnings("unchecked") Map<String,Integer>  prioridades = (Map<String,Integer>)  result[5];

                lblEspera.setText(String.format("%.0f", espera));
                lblConsulta.setText(String.format("%.0f", consulta));
                lblAtendidos.setText(String.valueOf(atend));

                XYChart.Series<String, Number> serieHoras = new XYChart.Series<>();
                horas.forEach((hora, total) -> {
                    if (hora >= 6 && hora <= 22)
                        serieHoras.getData().add(new XYChart.Data<>(hora + ":00", total));
                });
                graficaHoras.getData().setAll(serieHoras);

                XYChart.Series<Number, String> serieMedicos = new XYChart.Series<>();
                medicos.forEach((nombre, total) ->
                    serieMedicos.getData().add(new XYChart.Data<>(total, nombre)));
                graficaMedicos.getData().setAll(serieMedicos);

                ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();
                prioridades.forEach((nombre, total) -> {
                    if (total > 0) datosPie.add(new PieChart.Data(nombre + " (" + total + ")", total));
                });
                graficaPrioridades.setData(datosPie);
            }
        );
    }
}
