import java.util.ArrayList;
import java.util.List;

public class MoviePublisher implements Publisher {
    private List<Subscriber> subscribers = new ArrayList<>();

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void publish(Movie movie) {
        for (Subscriber s : subscribers) {
            s.notify(movie);
        }
    }
}
