import java.util.ArrayList;
import java.util.List;

public class MovieStorage {
    private static MovieStorage instance;
    private List<Movie> allMovies;
    
    private MovieStorage() {
        allMovies = new ArrayList<>();
    }
    
    public static MovieStorage getInstance() {
        if (instance == null) {
            instance = new MovieStorage();
        }
        return instance;
    }
    
    public void addMovie(Movie movie) {
        allMovies.add(movie);
    }
    
    public List<Movie> getAllMovies() {
        return new ArrayList<>(allMovies);
    }
    
    public List<Movie> getMoviesForDistributor(String distributor) {
        List<Movie> movies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getDistributor() != null && movie.getDistributor().equals(distributor)) {
                movies.add(movie);
            }
        }
        return movies;
    }
}
