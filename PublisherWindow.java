import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
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
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40, 0, 0, 0));
        root.getStyleClass().add("publisher-root");

        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, #00000022, 10, 0.2, 0, 4);");
        card.setAlignment(Pos.TOP_CENTER);

        // Header
        VBox header = new VBox(2);
        Label titleLabel = new Label("Publisher");
        titleLabel.getStyleClass().add("publisher-title");
        Label subtitle = new Label("Publica nuevas películas");
        subtitle.getStyleClass().add("publisher-subtitle");
        header.getChildren().addAll(titleLabel, subtitle);
        header.setAlignment(Pos.CENTER);

        // Form fields
        TextField titleField = new TextField();
        titleField.setPromptText("Título de la película");
        titleField.getStyleClass().add("input-field");

        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.getItems().addAll("Acción", "Comedia", "Drama", "Ciencia Ficción", "Animación");
        genreBox.setPromptText("Género");
        genreBox.getStyleClass().add("input-field");

        TextField yearField = new TextField();
        yearField.setPromptText("Año de lanzamiento");
        yearField.getStyleClass().add("input-field");

        TextArea descField = new TextArea();
        descField.setPromptText("Descripción");
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("input-field");

        // Imagen
        Label imageLabel = new Label("Poster de la película");
        imageLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Button uploadBtn = new Button("Subir imagen");
        uploadBtn.getStyleClass().add("btn-primary");
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.setVisible(false);
        final File[] selectedImage = {null};

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecciona una imagen");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Image img = new Image(file.toURI().toString());
                imageView.setImage(img);
                imageView.setVisible(true);
                selectedImage[0] = file;
            }
        });

        VBox imageBox = new VBox(8, imageLabel, uploadBtn, imageView);
        imageBox.setAlignment(Pos.CENTER);

        Button publishButton = new Button("🚀 Publicar Película");
        publishButton.getStyleClass().add("btn-primary");
        publishButton.setMaxWidth(Double.MAX_VALUE);

        publishButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String genre = genreBox.getValue() != null ? genreBox.getValue() : "";
            String year = yearField.getText().trim();
            String desc = descField.getText().trim();
            String poster = selectedImage[0] != null ? selectedImage[0].toURI().toString() : "";

            if (!title.isEmpty()) {
                Movie movie = new Movie(title, poster, genre, year, desc);
                publisher.publish(movie);
                titleField.clear();
                genreBox.setValue(null);
                yearField.clear();
                descField.clear();
                imageView.setImage(null);
                imageView.setVisible(false);
                selectedImage[0] = null;
            }
        });

        card.getChildren().addAll(header, titleField, genreBox, yearField, descField, imageBox, publishButton);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("modern.css").toExternalForm());
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
