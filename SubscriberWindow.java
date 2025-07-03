import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.*;

public class SubscriberWindow extends Application implements Subscriber {
    private List<Movie> movies = new ArrayList<>();
    private static Map<Movie, List<String>> reviews = new HashMap<>();
    private User user = new User("Usuario");

    // Referencias para actualizar la vista principal y de detalles
    private BorderPane root;
    private GridPane grid;
    private Movie selectedMovie = null;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        showMovieGrid();

        root.setStyle("-fx-border-color: black; -fx-border-width: 3;");
        Scene scene = new Scene(root, 900, 500);
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

            Button btn = new Button(movie.getTitle() + avgText);
            btn.setMinWidth(200);
            btn.setMinHeight(50);
            btn.setStyle("-fx-border-color: black; -fx-font-size: 16;");
            btn.setOnAction(e -> showMovieDetail(movie));
            int col = i % 2;
            int row = i / 2;
            grid.add(btn, col, row);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);

        root.setLeft(scrollPane);
        root.setCenter(null);
    }

    // Muestra la vista de detalles de la película seleccionada y sus comentarios (solo del usuario local)
    private void showMovieDetail(Movie movie) {
        selectedMovie = movie;

        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(30, 30, 30, 30));
        detailBox.setPrefWidth(400);

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

        Label title = new Label(movie.getTitle() + avgText);
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        ListView<String> commentList = new ListView<>();
        commentList.getItems().addAll(allReviews);
        commentList.setPrefHeight(300);

        HBox commentInput = new HBox(10);

        TextField userField = new TextField();
        userField.setPromptText("Usuario");

        TextField commentField = new TextField();
        commentField.setPromptText("Escribe tu comentario...");

        Spinner<Integer> ratingSpinner = new Spinner<>(1, 10, 5);
        ratingSpinner.setEditable(true);
        ratingSpinner.setPrefWidth(60);

        // Restringe la entrada a números entre 1 y 10
        TextFormatter<Integer> ratingFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,2}")) {
                try {
                    if (newText.isEmpty()) return change;
                    int value = Integer.parseInt(newText);
                    if (value >= 1 && value <= 10) {
                        return change;
                    }
                } catch (NumberFormatException ignored) {}
            }
            return null;
        });
        ratingSpinner.getEditor().setTextFormatter(ratingFormatter);

        // Sincroniza el valor del Spinner con el editor manual
        ratingSpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (!newVal.isEmpty()) {
                    int value = Integer.parseInt(newVal);
                    if (value >= 1 && value <= 10) {
                        ratingSpinner.getValueFactory().setValue(value);
                    }
                }
            } catch (NumberFormatException ignored) {}
        });

        Button addBtn = new Button("Agregar");

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

            // El comentario puede estar vacío, pero si quieres que sea obligatorio, descomenta:
            // if (text.isEmpty()) {
            //     commentField.setStyle("-fx-border-color: red;");
            //     valid = false;
            // } else {
            //     commentField.setStyle("");
            // }

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
                // commentField.setStyle(""); // si activas la validación de comentario

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

                // Opcional: también puedes actualizar el grid si quieres que el promedio se vea actualizado ahí
                showMovieGrid();
                // Pero vuelve a mostrar el detalle de la película actual
                showMovieDetail(movie);
            }
        };

        addBtn.setOnAction(e -> submitComment.run());

        // Permitir enviar con Enter desde cualquier campo
        userField.setOnAction(e -> submitComment.run());
        commentField.setOnAction(e -> submitComment.run());
        ratingSpinner.getEditor().setOnAction(e -> submitComment.run());

        commentInput.getChildren().addAll(userField, commentField, ratingSpinner, addBtn);

        detailBox.getChildren().addAll(title, commentList, commentInput);

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

    public static void main(String[] args) {
        launch(args);
    }
}
