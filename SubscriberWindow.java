import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;
import java.io.File;

public class SubscriberWindow extends Application implements Subscriber, DistributorListener {
    private String name = "Usuario Local";
    private Map<Movie, List<String>> reviews = new HashMap<>();
    private List<Movie> allMovies = new ArrayList<>();
    private VBox moviesContainer;
    private VBox subscriptionsContainer;
    private Label userStatusLabel;
    private UserManager userManager;
    private Stage primaryStage;
    private VBox mainRoot;
    private Movie selectedMovie = null;
    private VBox availableDistributorsBox;

    public SubscriberWindow() {
        userManager = UserManager.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Subscriber - Películas Disponibles");

        mainRoot = new VBox(10);
        mainRoot.setPadding(new Insets(10));
        mainRoot.getStyleClass().add("subscriber-root");

        VBox userPanel = createUserPanel();
        userPanel.getStyleClass().add("sidebar");

        TabPane tabPane = new TabPane();

        Tab moviesTab = new Tab("Películas");
        moviesTab.setClosable(false);

        VBox moviesRoot = new VBox(10);
        moviesRoot.setPadding(new Insets(20));

        Label moviesTitle = new Label("Películas Disponibles");
        moviesTitle.getStyleClass().add("white-label");
        moviesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        moviesContainer = new VBox(10);
        ScrollPane moviesScrollPane = new ScrollPane(moviesContainer);
        moviesScrollPane.setFitToWidth(true);
        moviesScrollPane.setPrefHeight(500);

        moviesRoot.getChildren().addAll(moviesTitle, moviesScrollPane);
        moviesTab.setContent(moviesRoot);

        Tab subscriptionsTab = new Tab("Suscripciones");
        subscriptionsTab.setClosable(false);

        VBox subscriptionsRoot = new VBox(10);
        subscriptionsRoot.setPadding(new Insets(20));

        Label subscriptionsTitle = new Label("Gestión de Suscripciones");
        subscriptionsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label availableLabel = new Label("Distribuidoras Disponibles:");
        availableLabel.getStyleClass().add("white-label");
        availableLabel.setStyle("-fx-font-weight: bold;");

        availableDistributorsBox = createAvailableDistributorsBox();

        HBox addDistBox = new HBox(10);
        TextField newDistField = new TextField();
        newDistField.setPromptText("Nueva distribuidora");
        Button addDistBtn = new Button("Agregar distribuidora");
        addDistBtn.setOnAction(e -> {
            String newDist = newDistField.getText().trim();
            if (!newDist.isEmpty()) {
                Distributor.addDistributor(newDist);
                newDistField.clear();
                refreshAvailableDistributors(subscriptionsRoot, availableDistributorsBox);
            }
        });
        addDistBox.getChildren().addAll(newDistField, addDistBtn);

        Label subscribedLabel = new Label("Mis Suscripciones:");
        subscribedLabel.getStyleClass().add("white-label");
        subscribedLabel.setStyle("-fx-font-weight: bold;");

        subscriptionsContainer = new VBox(5);

        subscriptionsRoot.getChildren().addAll(
            subscriptionsTitle,
            availableLabel,
            availableDistributorsBox,
            new Separator(),
            subscribedLabel,
            subscriptionsContainer
        );

        subscriptionsTab.setContent(new ScrollPane(subscriptionsRoot));
        tabPane.getTabs().addAll(moviesTab, subscriptionsTab);

        mainRoot.getChildren().addAll(userPanel, tabPane);

        Scene scene = new Scene(mainRoot, 850, 650);
        scene.getStylesheets().add("modern.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        Distributor.addListener(this);

        updateSubscriptionsView();
        updateMoviesView();
    }

    private void refreshAvailableDistributors(VBox subscriptionsRoot, VBox oldBox) {
        int idx = subscriptionsRoot.getChildren().indexOf(oldBox);
        VBox newBox = createAvailableDistributorsBox();
        subscriptionsRoot.getChildren().set(idx, newBox);
    }

    private VBox createUserPanel() {
        VBox userPanel = new VBox(10);

        Label userTitle = new Label("Gestión de Usuario");
        userTitle.getStyleClass().add("sidebar-title");

        userStatusLabel = new Label();
        updateUserStatusLabel();

        HBox userControls = new HBox(10);

        if (!userManager.isLoggedIn()) {
            TextField usernameField = new TextField();
            usernameField.setPromptText("Nombre de usuario");
            usernameField.getStyleClass().add("input-field");

            Button loginButton = new Button("Iniciar Sesión");
            loginButton.getStyleClass().add("btn-primary");
            loginButton.setOnAction(e -> {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    if (userManager.login(username)) {
                        refreshWindow();
                    }
                } else {
                    showAlert("Error", "Por favor ingrese un nombre de usuario válido.");
                }
            });

            ComboBox<String> usersCombo = new ComboBox<>();
            usersCombo.getItems().addAll(userManager.getRegisteredUsers());
            usersCombo.setPromptText("Usuarios registrados");
            usersCombo.getStyleClass().add("input-field");
            usersCombo.setOnAction(e -> {
                String selectedUser = usersCombo.getValue();
                if (selectedUser != null) {
                    usernameField.setText(selectedUser);
                }
            });

            userControls.getChildren().addAll(
                createStyledLabel("Usuario:"), usernameField,
                createStyledLabel("O seleccionar:"), usersCombo,
                loginButton
            );
        } else {
            Button logoutButton = new Button("Cerrar Sesión");
            logoutButton.getStyleClass().add("add-review-btn");
            logoutButton.setOnAction(e -> {
                userManager.logout();
                refreshWindow();
            });

            Button switchUserButton = new Button("Cambiar Usuario");
            switchUserButton.getStyleClass().add("btn-primary");
            switchUserButton.setOnAction(e -> {
                userManager.logout();
                refreshWindow();
            });

            userControls.getChildren().addAll(logoutButton, switchUserButton);
        }

        userPanel.getChildren().addAll(userTitle, userStatusLabel, userControls);
        return userPanel;
    }

    private void refreshWindow() {
        try {
            start(primaryStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserStatusLabel() {
        if (userManager.isLoggedIn()) {
            userStatusLabel.setText("Sesión iniciada como: " + userManager.getCurrentUser());
            userStatusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else {
            userStatusLabel.setText("No hay sesión iniciada");
            userStatusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        }
    }

    @Override
    public void notify(Movie movie) {
        updateMoviesView();
        System.out.println(name + " ha sido notificado de una nueva película: " + movie.getTitle());
    }

    @Override
    public void addReview(Movie movie, String review) {
        reviews.computeIfAbsent(movie, k -> new ArrayList<>()).add(review);
        System.out.println(name + " ha dejado una reseña para " + movie.getTitle() + ": " + review);
    }

    @Override
    public void addReview(Movie movie, String comment, int rating) {
        String username = userManager.isLoggedIn() ? 
                         userManager.getCurrentUser() : 
                         "Usuario Anónimo";
        movie.addReview(comment, rating, username);
        System.out.println(username + " ha dejado una reseña para " + movie.getTitle() + " con calificación " + rating + "/10: " + comment);
    }

    @Override
    public void subscribeToDistributor(String distributor) {
        userManager.subscribeCurrentUserToDistributor(distributor);
        System.out.println((userManager.getCurrentUser() != null ? userManager.getCurrentUser() : "Usuario") + " se ha suscrito a: " + distributor);
    }

    @Override
    public void unsubscribeFromDistributor(String distributor) {
        userManager.unsubscribeCurrentUserFromDistributor(distributor);
        System.out.println((userManager.getCurrentUser() != null ? userManager.getCurrentUser() : "Usuario") + " se ha desuscrito de: " + distributor);
    }

    @Override
    public Set<String> getSubscribedDistributors() {
        return userManager.getCurrentUserSubscriptions();
    }

    @Override
    public boolean canViewMovie(Movie movie) {
        return movie.getDistributor() == null || userManager.isCurrentUserSubscribedTo(movie.getDistributor());
    }

    private void updateMoviesView() {
        moviesContainer.getChildren().clear();

        Set<String> currentUserSubscriptions = userManager.getCurrentUserSubscriptions();

        if (currentUserSubscriptions.isEmpty()) {
            Label noSubscriptionsLabel = new Label("No tienes suscripciones activas. Ve a la pestaña 'Suscripciones' para suscribirte a distribuidoras.");
            noSubscriptionsLabel.setStyle("-fx-text-fill: #666;");
            moviesContainer.getChildren().add(noSubscriptionsLabel);
            return;
        }

        List<Movie> allStoredMovies = MovieStorage.getInstance().getAllMovies();
        List<Movie> availableMovies = new ArrayList<>();

        for (Movie movie : allStoredMovies) {
            if (canViewMovie(movie)) {
                availableMovies.add(movie);
            }
        }

        if (availableMovies.isEmpty()) {
            Label noMoviesLabel = new Label("No hay películas disponibles para tus suscripciones actuales.");
            noMoviesLabel.setStyle("-fx-text-fill: #666;");
            moviesContainer.getChildren().add(noMoviesLabel);
        } else {
            GridPane grid = new GridPane();
            grid.getStyleClass().add("movie-grid");
            grid.setHgap(36);
            grid.setVgap(36);

            int colCount = 4;
            for (int i = 0; i < availableMovies.size(); i++) {
                Movie movie = availableMovies.get(i);

                VBox card = new VBox(8);
                card.getStyleClass().add("movie-card");
                card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

                ImageView poster = new ImageView();
                try {
                    String imagePath = movie.getImageUrl();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        Image image;
                        if (imagePath.startsWith("http")) {
                            image = new Image(imagePath, 180, 180, false, true);
                        } else {
                            File imageFile = new File(imagePath);
                            if (imageFile.exists()) {
                                image = new Image(imageFile.toURI().toString(), 180, 180, false, true);
                            } else {
                                image = createDefaultImage();
                            }
                        }
                        poster.setImage(image);
                    } else {
                        poster.setImage(createDefaultImage());
                    }
                } catch (Exception e) {
                    poster.setImage(createDefaultImage());
                }
                poster.setFitWidth(180);
                poster.setFitHeight(180);
                poster.getStyleClass().add("movie-poster");

                Label titleLabel = new Label(movie.getTitle());
                titleLabel.getStyleClass().add("movie-title-label");

                double avgRating = movie.getAverageRating();
                String avgText = avgRating > 0 ? String.format("%.1f ★", avgRating) : "N/A";
                Label ratingLabel = new Label(avgText);
                ratingLabel.getStyleClass().add("movie-rating-label");

                card.getChildren().addAll(poster, titleLabel, ratingLabel);

                card.setOnMouseClicked(e -> showMovieDetailInMain(movie));

                int col = i % colCount;
                int row = i / colCount;
                grid.add(card, col, row);
            }

            moviesContainer.getChildren().add(grid);
        }
    }

    private void showMovieDetailInMain(Movie movie) {
        selectedMovie = movie;
        moviesContainer.getChildren().clear();
        VBox card = createMovieDetailCard(movie);
        moviesContainer.getChildren().add(card);
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Volver");
        backBtn.getStyleClass().add("btn-primary");
        backBtn.setOnAction(e -> {
            selectedMovie = null;
            updateMoviesView();
        });
        return backBtn;
    }

    private VBox createMovieDetailCard(Movie movie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movie-detail-card");

        card.getChildren().add(createBackButton());

        HBox titleImageBox = new HBox(20);

        VBox titleInfoBox = new VBox(5);
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("movie-title");

        Label ratingLabel = new Label();
        ratingLabel.getStyleClass().add("movie-rating");

        Runnable updateRating = () -> {
            double avgRating = movie.getAverageRating();
            String ratingText = avgRating > 0 ? String.format("%.1f/10", avgRating) : "Sin calificación";
            ratingLabel.setText("⭐ " + ratingText + " (" + movie.getReviewCount() + " reseñas)");
        };
        updateRating.run();

        VBox movieInfoBox = new VBox(5);
        movieInfoBox.getStyleClass().add("blue-box");
        Label distributorLabel = new Label("Distribuidora: " + movie.getDistributor());
        distributorLabel.getStyleClass().add("meta-label");
        Label genreLabel = new Label("Género: " + movie.getGenre());
        genreLabel.getStyleClass().add("meta-label");
        Label yearLabel = new Label("Año: " + movie.getYear());
        yearLabel.getStyleClass().add("meta-label");
        movieInfoBox.getChildren().addAll(distributorLabel, genreLabel, yearLabel);

        titleInfoBox.getChildren().addAll(titleLabel, ratingLabel, movieInfoBox);

        ImageView imageView = new ImageView();
        try {
            String imagePath = movie.getImageUrl();
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image;
                if (imagePath.startsWith("http")) {
                    image = new Image(imagePath, 180, 180, false, true);
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        image = new Image(imageFile.toURI().toString(), 180, 180, false, true);
                    } else {
                        image = createDefaultImage();
                    }
                }
                imageView.setImage(image);
            } else {
                imageView.setImage(createDefaultImage());
            }
        } catch (Exception e) {
            imageView.setImage(createDefaultImage());
        }
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.getStyleClass().add("movie-poster");

        titleImageBox.getChildren().addAll(imageView, titleInfoBox);

        VBox descriptionBox = new VBox(5);
        descriptionBox.getStyleClass().add("green-box");
        Label descriptionLabel = new Label(movie.getDescription());
        descriptionLabel.getStyleClass().add("desc-label");
        descriptionLabel.setWrapText(true);
        descriptionBox.getChildren().add(descriptionLabel);

        VBox reviewsBox = new VBox(5);
        reviewsBox.getStyleClass().add("review-list");
        Label reviewsTitle = new Label("Reseñas:");
        reviewsTitle.getStyleClass().add("stat-label");
        reviewsBox.getChildren().add(reviewsTitle);

        Runnable updateReviews = () -> {
            reviewsBox.getChildren().setAll(reviewsTitle);
            for (Movie.Review review : movie.getReviews()) {
                VBox reviewBox = new VBox(2);
                reviewBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #e1e8ed; -fx-border-width: 1;");
                HBox reviewHeader = new HBox(10);
                Label ratingReviewLabel = new Label("⭐ " + review.getRating() + "/10");
                ratingReviewLabel.getStyleClass().add("movie-rating");
                Label usernameLabel = new Label("por " + review.getUsername());
                usernameLabel.getStyleClass().add("stat-label");
                reviewHeader.getChildren().addAll(ratingReviewLabel, usernameLabel);
                Label commentLabel = new Label(review.getComment());
                commentLabel.setWrapText(true);
                commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                reviewBox.getChildren().addAll(reviewHeader, commentLabel);
                reviewsBox.getChildren().add(reviewBox);
            }
        };
        updateReviews.run();

        VBox addReviewBox = new VBox(5);
        addReviewBox.getStyleClass().add("review-form");
        Label addReviewTitle = new Label("Agregar reseña:");
        addReviewTitle.getStyleClass().add("stat-label");

        if (!userManager.isLoggedIn()) {
            Label loginRequiredLabel = new Label("Debes iniciar sesión para agregar reseñas.");
            loginRequiredLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-style: italic;");
            addReviewBox.getChildren().addAll(addReviewTitle, loginRequiredLabel);
        } else {
            HBox ratingBox = new HBox(10);
            Label ratingPrompt = new Label("Calificación (1-10):");
            ratingPrompt.getStyleClass().add("stat-label");

            Spinner<Integer> ratingSpinner = new Spinner<>(1, 10, 5);
            ratingSpinner.setEditable(true);
            ratingSpinner.getStyleClass().add("rating-spinner");

            ratingBox.getChildren().addAll(ratingPrompt, ratingSpinner);

            TextArea reviewArea = new TextArea();
            reviewArea.setPromptText("Escribe tu comentario...");
            reviewArea.setPrefRowCount(2);
            reviewArea.getStyleClass().add("input-field");

            Button reviewButton = new Button("Agregar Reseña");
            reviewButton.getStyleClass().add("add-review-btn");
            reviewButton.setOnAction(e -> {
                String comment = reviewArea.getText().trim();
                int rating = ratingSpinner.getValue();
                if (!comment.isEmpty()) {
                    addReview(movie, comment, rating);
                    reviewArea.clear();
                    ratingSpinner.getValueFactory().setValue(5);
                    updateReviews.run();
                    updateRating.run();
                }
            });

            addReviewBox.getChildren().addAll(addReviewTitle, ratingBox, reviewArea, reviewButton);
        }

        card.getChildren().addAll(
            titleImageBox,
            descriptionBox,
            reviewsBox,
            new Separator(),
            addReviewBox
        );

        return card;
    }

    @Override
    public void onDistributorsChanged() {
        if (availableDistributorsBox != null && availableDistributorsBox.getParent() != null) {
            VBox parent = (VBox) availableDistributorsBox.getParent();
            int idx = parent.getChildren().indexOf(availableDistributorsBox);
            availableDistributorsBox = createAvailableDistributorsBox();
            parent.getChildren().set(idx, availableDistributorsBox);
        }
    }

    private Image createDefaultImage() {
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==", 150, 200, false, true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateSubscriptionsView() {
        subscriptionsContainer.getChildren().clear();

        Set<String> currentUserSubscriptions = userManager.getCurrentUserSubscriptions();

        if (currentUserSubscriptions.isEmpty()) {
            Label noSubscriptionsLabel = new Label("No tienes suscripciones activas.");
            noSubscriptionsLabel.setStyle("-fx-text-fill: #666;");
            subscriptionsContainer.getChildren().add(noSubscriptionsLabel);
        } else {
            for (String distributor : currentUserSubscriptions) {
                HBox subscriptionBox = new HBox(10);
                Label distributorLabel = new Label(distributor);
                Button unsubscribeButton = new Button("Desuscribirse");
                unsubscribeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                unsubscribeButton.setOnAction(e -> {
                    unsubscribeFromDistributor(distributor);
                    updateSubscriptionsView();
                    updateMoviesView();
                });
                subscriptionBox.getChildren().addAll(distributorLabel, unsubscribeButton);
                subscriptionsContainer.getChildren().add(subscriptionBox);
            }
        }
    }

    private VBox createAvailableDistributorsBox() {
        VBox availableDistributors = new VBox(5);
        for (String distributor : Distributor.getAvailableDistributors()) {
            HBox distributorBox = new HBox(10);
            Label distributorLabel = new Label(distributor);
            Button subscribeButton = new Button("Suscribirse");
            subscribeButton.setOnAction(e -> {
                subscribeToDistributor(distributor);
                updateSubscriptionsView();
                updateMoviesView();
            });
            distributorBox.getChildren().addAll(distributorLabel, subscribeButton);
            availableDistributors.getChildren().add(distributorBox);
        }
        return availableDistributors;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("white-label");
        return label;
    }
}


