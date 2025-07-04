import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class User implements Subscriber {
    private String name;
    private Map<Movie, List<String>> reviews = new HashMap<>();

    public User(String name) {
        this.name = name;
    }

    @Override
    public void notify(Movie movie) {
        System.out.println(name + " ha sido notificado de una nueva película: " + movie.getTitle());
    }

    @Override
    public void addReview(Movie movie, String review) {
        reviews.computeIfAbsent(movie, k -> new ArrayList<>()).add(review);
        System.out.println(name + " ha dejado una reseña para " + movie.getTitle() + ": " + review);
    }

    
}