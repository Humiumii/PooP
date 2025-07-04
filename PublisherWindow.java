import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class PublisherWindow extends Application {
    private List<Subscriber> subscribers;
    private MoviePublisher publisher;
    
    public PublisherWindow(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
        this.publisher = new MoviePublisher();
        
        for (Subscriber subscriber : subscribers) {
            publisher.subscribe(subscriber);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Publisher - Subir Películas");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("sidebar");
        
        VBox.setVgrow(root, Priority.ALWAYS);
        
        Label titleLabel = new Label("Subir Nueva Película");
        titleLabel.getStyleClass().add("sidebar-title");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(10));
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        col1.setPrefWidth(120);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setMinWidth(200);
        
        formGrid.getColumnConstraints().addAll(col1, col2);
        
        int row = 0;
        
        Label titleFieldLabel = new Label("Título:");
        titleFieldLabel.getStyleClass().add("stat-label");
        TextField titleField = new TextField();
        titleField.setPromptText("Título de la película");
        titleField.getStyleClass().add("input-field");
        titleField.setMaxWidth(Double.MAX_VALUE);
        formGrid.add(titleFieldLabel, 0, row);
        formGrid.add(titleField, 1, row++);
        
        Label imageFieldLabel = new Label("Imagen:");
        imageFieldLabel.getStyleClass().add("stat-label");
        HBox imageBox = new HBox(10);
        TextField imageUrlField = new TextField();
        imageUrlField.setPromptText("URL de la imagen o ruta local");
        imageUrlField.getStyleClass().add("input-field");
        HBox.setHgrow(imageUrlField, Priority.ALWAYS);
        
        Button browseButton = new Button("Buscar");
        browseButton.getStyleClass().add("btn-primary");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                imageUrlField.setText(selectedFile.getAbsolutePath());
            }
        });
        
        imageBox.getChildren().addAll(imageUrlField, browseButton);
        formGrid.add(imageFieldLabel, 0, row);
        formGrid.add(imageBox, 1, row++);
        
        Label genreFieldLabel = new Label("Género:");
        genreFieldLabel.getStyleClass().add("stat-label");
        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll("Acción", "Aventura", "Comedia", "Drama", "Terror", "Ciencia Ficción", "Romance", "Thriller");
        genreCombo.setPromptText("Seleccionar género");
        genreCombo.getStyleClass().add("input-field");
        genreCombo.setMaxWidth(Double.MAX_VALUE);
        formGrid.add(genreFieldLabel, 0, row);
        formGrid.add(genreCombo, 1, row++);
        
        Label yearFieldLabel = new Label("Año:");
        yearFieldLabel.getStyleClass().add("stat-label");
        TextField yearField = new TextField();
        yearField.setPromptText("Año de estreno");
        yearField.getStyleClass().add("input-field");
        yearField.setMaxWidth(Double.MAX_VALUE);
        formGrid.add(yearFieldLabel, 0, row);
        formGrid.add(yearField, 1, row++);
        
        Label descFieldLabel = new Label("Descripción:");
        descFieldLabel.getStyleClass().add("stat-label");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Descripción de la película");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setMaxHeight(100);
        descriptionArea.getStyleClass().add("input-field");
        formGrid.add(descFieldLabel, 0, row);
        formGrid.add(descriptionArea, 1, row++);
        
        Label distFieldLabel = new Label("Distribuidora:");
        distFieldLabel.getStyleClass().add("stat-label");
        ComboBox<String> distributorCombo = new ComboBox<>();
        distributorCombo.getItems().addAll(Distributor.getAvailableDistributors());
        distributorCombo.setPromptText("Seleccionar distribuidora");
        distributorCombo.getStyleClass().add("input-field");
        distributorCombo.setMaxWidth(Double.MAX_VALUE);
        formGrid.add(distFieldLabel, 0, row);
        formGrid.add(distributorCombo, 1, row++);
        
        VBox newDistributorSection = new VBox(10);
        newDistributorSection.getStyleClass().add("review-form");
        
        Label newDistLabel = new Label("Agregar nueva distribuidora:");
        newDistLabel.getStyleClass().add("stat-label");
        
        HBox newDistBox = new HBox(10);
        TextField newDistributorField = new TextField();
        newDistributorField.setPromptText("Nueva distribuidora");
        newDistributorField.getStyleClass().add("input-field");
        HBox.setHgrow(newDistributorField, Priority.ALWAYS);
        
        Button addDistributorButton = new Button("Agregar");
        addDistributorButton.getStyleClass().add("btn-primary");
        addDistributorButton.setOnAction(e -> {
            String newDistributor = newDistributorField.getText().trim();
            if (!newDistributor.isEmpty()) {
                Distributor.addDistributor(newDistributor);
                distributorCombo.getItems().clear();
                distributorCombo.getItems().addAll(Distributor.getAvailableDistributors());
                distributorCombo.setValue(newDistributor);
                newDistributorField.clear();
                showAlert("Éxito", "Distribuidora agregada exitosamente!");
            }
        });
        
        newDistBox.getChildren().addAll(newDistributorField, addDistributorButton);
        newDistributorSection.getChildren().addAll(newDistLabel, newDistBox);
        
        Button publishButton = new Button("Publicar Película");
        publishButton.getStyleClass().add("add-review-btn");
        publishButton.setMaxWidth(Double.MAX_VALUE);
        publishButton.setPrefHeight(50);
        publishButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String imageUrl = imageUrlField.getText().trim();
            String genre = genreCombo.getValue();
            String year = yearField.getText().trim();
            String description = descriptionArea.getText().trim();
            String distributor = distributorCombo.getValue();
            
            if (title.isEmpty() || imageUrl.isEmpty() || genre == null || year.isEmpty() || distributor == null) {
                showAlert("Error", "Por favor, complete todos los campos incluyendo la distribuidora.");
                return;
            }
            
            Movie movie = new Movie(title, imageUrl, genre, year, description, distributor);
            publisher.publish(movie);
            
            showAlert("Éxito", "Película publicada exitosamente!");
            
            titleField.clear();
            imageUrlField.clear();
            genreCombo.setValue(null);
            yearField.clear();
            descriptionArea.clear();
            distributorCombo.setValue(null);
        });
        
        root.getChildren().addAll(
            titleLabel,
            formGrid,
            newDistributorSection,
            publishButton
        );
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("subscriber-root");
        
        Scene scene = new Scene(scrollPane, 600, 700);
        scene.getStylesheets().add("modern.css");
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
