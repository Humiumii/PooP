import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class PublisherWindow extends Application {
    private MoviePublisher publisher = new MoviePublisher();
    private List<Subscriber> subscribers;

    public PublisherWindow() {
        // Constructor vacío para JavaFX
    }

    public PublisherWindow(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
        for (Subscriber s : subscribers) {
            publisher.subscribe(s);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        TextField titleField = new TextField();
        titleField.setPromptText("Título de la película");

        Button publishButton = new Button("PUBLICAR");
        Runnable publishAction = () -> {
            String title = titleField.getText();
            if (!title.isEmpty()) {
                Movie movie = new Movie(title, "");
                publisher.publish(movie);
                titleField.clear();
            }
        };
        publishButton.setOnAction(e -> publishAction.run());
        titleField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                publishAction.run();
            }
        });

        HBox hbox = new HBox(20, titleField, publishButton);
        hbox.setStyle("-fx-padding: 40 0 0 40;");

        StackPane root = new StackPane(hbox);
        root.setStyle("-fx-border-color: red; -fx-border-width: 3;");

        Scene scene = new Scene(root, 600, 350);
        primaryStage.setTitle("Publisher - Publicar Película");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void publishMovie(String title) {
        if (!title.isEmpty()) {
            Movie movie = new Movie(title, "");
            publisher.publish(movie);
        }
    }

    public static void main(String[] args) {
        // Para pruebas manuales, solo Publisher
        launch(args);
    }
}
