package triagesystem;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class LoaderOverlay {

    private static final String OVERLAY_ID = "loader-overlay";

    // ── Mostrar / ocultar ─────────────────────────────────────────────────────

    public static void mostrar(StackPane sp) {
        if (sp == null) return;
        ocultar(sp); // evitar duplicados

        VBox overlay = new VBox(12);
        overlay.setId(OVERLAY_ID);
        overlay.setAlignment(Pos.CENTER);
        overlay.setMouseTransparent(false); // bloquea clics al contenido inferior
        overlay.setStyle(
            "-fx-background-color: rgba(15,23,42,0.45);" +
            "-fx-background-radius: 0;"
        );

        ProgressIndicator spinner = new ProgressIndicator(-1);
        spinner.setPrefSize(52, 52);
        spinner.setStyle("-fx-accent: #2563eb;");

        Label lbl = new Label("Cargando...");
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600;");

        overlay.getChildren().addAll(spinner, lbl);
        StackPane.setAlignment(overlay, Pos.CENTER);
        sp.getChildren().add(overlay);
    }

    public static void ocultar(StackPane sp) {
        if (sp == null) return;
        sp.getChildren().removeIf(n -> OVERLAY_ID.equals(n.getId()));
    }

    // ── Buscar el StackPane raíz desde cualquier nodo ─────────────────────────

    public static StackPane buscarRootPane(Node ref) {
        if (ref == null || ref.getScene() == null) return null;
        Node n = ref.getScene().lookup("#contenidoPrincipal");
        return n instanceof StackPane sp ? sp : null;
    }

    // ── Helper genérico: ejecutar DB en hilo de fondo ─────────────────────────

    /**
     * Muestra el overlay, ejecuta {@code tarea} en un hilo de fondo,
     * luego oculta el overlay y llama {@code alTerminar} en el hilo de JavaFX.
     *
     * @param ref       Cualquier nodo @FXML del controlador (para encontrar la escena)
     * @param tarea     Operación de DB (hilo de fondo) — NO puede tocar la UI
     * @param alTerminar Callback con el resultado (hilo de JavaFX) — aquí sí actualiza la UI
     * @param alFallar  Callback si la tarea lanza excepción (puede ser null)
     */
    public static <T> void runAsync(Node ref,
                                    Callable<T> tarea,
                                    Consumer<T> alTerminar,
                                    Runnable alFallar) {
        StackPane sp = buscarRootPane(ref);
        Platform.runLater(() -> mostrar(sp));

        Task<T> task = new Task<>() {
            @Override protected T call() throws Exception { return tarea.call(); }
        };

        task.setOnSucceeded(e -> {
            ocultar(sp);
            if (alTerminar != null) alTerminar.accept(task.getValue());
        });

        task.setOnFailed(e -> {
            ocultar(sp);
            if (alFallar != null) alFallar.run();
            task.getException().printStackTrace();
        });

        Thread t = new Thread(task, "db-task");
        t.setDaemon(true);
        t.start();
    }

    /** Sobrecarga sin callback de error. */
    public static <T> void runAsync(Node ref, Callable<T> tarea, Consumer<T> alTerminar) {
        runAsync(ref, tarea, alTerminar, null);
    }
}
