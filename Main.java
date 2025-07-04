import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        List<Subscriber> subscribers = new ArrayList<>();
        SubscriberWindow subscriberWindow = new SubscriberWindow();
        subscribers.add(subscriberWindow);

        PublisherWindow publisherWindow = new PublisherWindow(subscribers);
        Stage pubStage = new Stage();
        publisherWindow.start(pubStage);

        Stage subStage = new Stage();
        subscriberWindow.start(subStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}