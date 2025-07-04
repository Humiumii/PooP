import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.*;

public class SubscriberWindow extends Application implements Subscriber {
    private List<Movie> movies = new ArrayList<>();
    private static Map<Movie, List<String>> reviews = new HashMap<>();
    private User user = new User("Usuario");

   
    private BorderPane root;
    private GridPane grid;
    private Movie selectedMovie = null;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.getStyleClass().add("subscriber-root");
        showMovieGrid();

        
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("modern.css").toExternalForm());

        primaryStage.setTitle("Subscriber - Películas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Muestra el grid de películas en la ventana principal
    private void showMovieGrid() {
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(40);
        grid.setVgap(15);

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);

            // Calcular promedio de notas para la película
            List<String> allReviews = reviews.getOrDefault(movie, new ArrayList<>());
            double avg = 0;
            int count = 0;
            for (String review : allReviews) {
                // Buscar el patrón " (X/10):"
                int idx1 = review.indexOf('(');
                int idx2 = review.indexOf("/10):");
                if (idx1 != -1 && idx2 != -1 && idx2 > idx1 + 1) {
                    try {
                        int rating = Integer.parseInt(review.substring(idx1 + 1, idx2));
                        avg += rating;
                        count++;
                    } catch (NumberFormatException ignored) {}
                }
            }
            String avgText = count > 0 ? String.format(" [%.1f/10]", avg / count) : " [N/A]";

            // --- Nuevo: VBox con imagen y botón ---
            VBox movieBox = new VBox(8);
            movieBox.setAlignment(Pos.CENTER);
            movieBox.setPrefWidth(220);

            // Imagen del póster (miniatura)
            ImageView posterView = null;
            if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
                try {
                    Image posterImg = new Image(movie.getImageUrl(), 90, 130, true, true);
                    posterView = new ImageView(posterImg);
                    posterView.setFitWidth(90);
                    posterView.setFitHeight(130);
                    posterView.setPreserveRatio(true);
                } catch (Exception ignored) {}
            }
            if (posterView != null) {
                movieBox.getChildren().add(posterView);
            }

            Button btn = new Button(movie.getTitle() + avgText);
            btn.getStyleClass().add("movie-list-btn");
            btn.setMinWidth(180);
            btn.setMinHeight(40);
            btn.setOnAction(e -> showMovieDetail(movie));
            movieBox.getChildren().add(btn);

            int col = i % 1;
            int row = i;
            grid.add(movieBox, col, row);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(650);
        scrollPane.setPrefWidth(320); // Más ancho
        scrollPane.getStyleClass().add("movie-list-scroll");

        VBox leftBox = new VBox();
        Label header = new Label("🎬 Películas");
        header.getStyleClass().add("sidebar-title");
        leftBox.getChildren().addAll(header, scrollPane);
        leftBox.setSpacing(10);
        leftBox.setPadding(new Insets(20, 10, 20, 20));
        leftBox.getStyleClass().add("sidebar");
        leftBox.setPrefWidth(340); // Más ancho

        root.setLeft(leftBox);
        root.setCenter(null);
    }

    // Muestra la vista de detalles de la película seleccionada y sus comentarios (solo del usuario local)
    private void showMovieDetail(Movie movie) {
        selectedMovie = movie;

        VBox detailBox = new VBox(20);
        detailBox.setPadding(new Insets(30));
        detailBox.setPrefWidth(600);
        detailBox.getStyleClass().add("movie-detail-card");

        // Calcular promedio inicial
        List<String> allReviews = reviews.getOrDefault(movie, new ArrayList<>());
        double avg = 0;
        int count = 0;
        for (String review : allReviews) {
            int idx1 = review.indexOf('(');
            int idx2 = review.indexOf("/10):");
            if (idx1 != -1 && idx2 != -1 && idx2 > idx1 + 1) {
                try {
                    int rating = Integer.parseInt(review.substring(idx1 + 1, idx2));
                    avg += rating;
                    count++;
                } catch (NumberFormatException ignored) {}
            }
        }
        String avgText = count > 0 ? String.format(" [%.1f/10]", avg / count) : " [N/A]";

        // Movie title and rating
        HBox titleBox = new HBox(10);
        Label title = new Label("🎬 " + movie.getTitle());
        title.getStyleClass().add("movie-title");
        Label ratingLabel = new Label(avgText.replace("[", "").replace("]", ""));
        ratingLabel.getStyleClass().add("movie-rating");
        titleBox.getChildren().addAll(title, ratingLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Recuadro azul: año y género
        HBox metaBox = new HBox(15);
        metaBox.getStyleClass().add("blue-box");
        metaBox.setPadding(new Insets(10));
        metaBox.setAlignment(Pos.CENTER_LEFT);

        Label yearLabel = new Label("Año: " + (movie.getYear() != null ? movie.getYear() : "N/A"));
        yearLabel.getStyleClass().add("meta-label");
        Label genreLabel = new Label("Género: " + (movie.getGenre() != null ? movie.getGenre() : "N/A"));
        genreLabel.getStyleClass().add("meta-label");
        metaBox.getChildren().addAll(yearLabel, genreLabel);

        // Imagen del póster (debajo del metaBox)
        ImageView posterView = null;
        if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            try {
                Image posterImg = new Image(movie.getImageUrl(), 180, 260, true, true);
                posterView = new ImageView(posterImg);
                posterView.setFitWidth(180);
                posterView.setFitHeight(260);
                posterView.setPreserveRatio(true);
            } catch (Exception ignored) {}
        }

        // Recuadro verde: descripción
        VBox descBox = new VBox();
        descBox.getStyleClass().add("green-box");
        descBox.setPadding(new Insets(15));
        Label descLabel = new Label(movie.getDescription() != null ? movie.getDescription() : "Sin descripción.");
        descLabel.getStyleClass().add("desc-label");
        descBox.getChildren().add(descLabel);

        // Review stats
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        Label countLabel = new Label(allReviews.size() + " Reseñas");
        countLabel.getStyleClass().add("stat-label");
        Label avgLabel = new Label("Promedio: " + (count > 0 ? String.format("%.1f", avg / count) : "N/A"));
        avgLabel.getStyleClass().add("stat-label");
        statsBox.getChildren().addAll(countLabel, avgLabel);

        // Reviews List
        ListView<String> commentList = new ListView<>();
        commentList.getItems().addAll(allReviews);
        commentList.setPrefHeight(220);
        commentList.getStyleClass().add("review-list");

        // Review input
        VBox reviewForm = new VBox(10);
        reviewForm.getStyleClass().add("review-form");
        HBox commentInput = new HBox(10);

        TextField userField = new TextField();
        userField.setPromptText("Tu nombre");
        userField.getStyleClass().add("input-field");
        userField.setPrefWidth(180);

        Spinner<Integer> ratingSpinner = new Spinner<>(1, 10, 5);
        ratingSpinner.setEditable(true);
        ratingSpinner.setPrefWidth(80);
        ratingSpinner.getStyleClass().add("rating-spinner");

        TextField commentField = new TextField();
        commentField.setPromptText("Escribe tu comentario...");
        commentField.getStyleClass().add("input-field");
        commentField.setPrefWidth(220);

        Button addBtn = new Button("✨ Agregar Reseña");
        addBtn.getStyleClass().add("add-review-btn");

        Runnable submitComment = () -> {
            String username = userField.getText().trim();
            String text = commentField.getText().trim();
            String ratingText = ratingSpinner.getEditor().getText().trim();
            boolean valid = true;

            // Validación de campos
            if (username.isEmpty()) {
                userField.setStyle("-fx-border-color: red;");
                valid = false;
            } else {
                userField.setStyle("");
            }
            int rating = -1;
            try {
                rating = Integer.parseInt(ratingText);
                if (rating < 1 || rating > 10) {
                    ratingSpinner.getEditor().setStyle("-fx-border-color: red;");
                    valid = false;
                } else {
                    ratingSpinner.getEditor().setStyle("");
                }
            } catch (NumberFormatException e) {
                ratingSpinner.getEditor().setStyle("-fx-border-color: red;");
                valid = false;
            }

            if (valid) {
                String userComment = username + " (" + rating + "/10): " + text;
                user.addReview(movie, userComment);
                reviews.computeIfAbsent(movie, k -> new ArrayList<>()).add(userComment);
                commentList.getItems().add(userComment);
                commentField.clear();
                userField.clear();
                ratingSpinner.getValueFactory().setValue(5);
                userField.setStyle("");
                ratingSpinner.getEditor().setStyle("");

                // Actualizar el promedio en el título
                List<String> updatedReviews = reviews.getOrDefault(movie, new ArrayList<>());
                double newAvg = 0;
                int newCount = 0;
                for (String review : updatedReviews) {
                    int idx1 = review.indexOf('(');
                    int idx2 = review.indexOf("/10):");
                    if (idx1 != -1 && idx2 != -1 && idx2 > idx1 + 1) {
                        try {
                            int r = Integer.parseInt(review.substring(idx1 + 1, idx2));
                            newAvg += r;
                            newCount++;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                String newAvgText = newCount > 0 ? String.format(" [%.1f/10]", newAvg / newCount) : " [N/A]";
                title.setText(movie.getTitle() + newAvgText);

                showMovieGrid();
                showMovieDetail(movie);
            }
        };

        addBtn.setOnAction(e -> submitComment.run());
        userField.setOnAction(e -> submitComment.run());
        commentField.setOnAction(e -> submitComment.run());
        ratingSpinner.getEditor().setOnAction(e -> submitComment.run());

        commentInput.getChildren().addAll(userField, ratingSpinner, commentField, addBtn);
        reviewForm.getChildren().addAll(new Label("💬 Agregar tu Reseña"), commentInput);

        detailBox.getChildren().clear();
        detailBox.getChildren().add(titleBox);
        detailBox.getChildren().add(metaBox); // recuadro azul
        if (posterView != null) {
            detailBox.getChildren().add(posterView);
        }
        detailBox.getChildren().add(descBox); // recuadro verde
        detailBox.getChildren().addAll(statsBox, commentList, reviewForm);

        root.setCenter(detailBox);
    }

    // Método del modelo Publish-Subscribe
    @Override
    public void notify(Movie movie) {
        movies.add(movie);
        reviews.putIfAbsent(movie, new ArrayList<>());
        if (grid != null) {
            showMovieGrid();
        }
    }

    @Override
    public void addReview(Movie movie, String review) {
        user.addReview(movie, review);
        reviews.computeIfAbsent(movie, k -> new ArrayList<>()).add(review);
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
