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
        MovieStorage.getInstance().addMovie(movie);
        
        for (Subscriber s : subscribers) {
            if (s.canViewMovie(movie)) {
                s.notify(movie);
            }
        }
    }

    public List<Movie> getMoviesForSubscriber(Subscriber subscriber, List<Movie> allMovies) {
        List<Movie> availableMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (subscriber.canViewMovie(movie)) {
                availableMovies.add(movie);
            }
        }
        return availableMovies;
    }
}
        
