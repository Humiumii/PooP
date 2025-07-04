import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class User implements Subscriber {
    private String name;
    private Map<Movie, List<String>> reviews = new HashMap<>();

    public User(String name) {
        this.name = name;
    }

    @Override
    public void notify(Movie movie) {
        if (movie.getDistributor() == null || UserManager.getInstance().isCurrentUserSubscribedTo(movie.getDistributor())) {
            System.out.println(name + " ha sido notificado de una nueva película: " + movie.getTitle());
        }
    }

    @Override
    public void addReview(Movie movie, String review) {
        reviews.computeIfAbsent(movie, k -> new ArrayList<>()).add(review);
        System.out.println(name + " ha dejado una reseña para " + movie.getTitle() + ": " + review);
    }

    @Override
    public void addReview(Movie movie, String comment, int rating) {
        String username = UserManager.getInstance().isLoggedIn() ? 
                         UserManager.getInstance().getCurrentUser() : 
                         "Usuario Anónimo";
        movie.addReview(comment, rating, username);
        System.out.println(username + " ha dejado una reseña para " + movie.getTitle() + " con calificación " + rating + "/10: " + comment);
    }

    public void subscribeToDistributor(String distributor) {
        UserManager.getInstance().subscribeCurrentUserToDistributor(distributor);
        System.out.println(name + " se ha suscrito a: " + distributor);
    }

    public void unsubscribeFromDistributor(String distributor) {
        UserManager.getInstance().unsubscribeCurrentUserFromDistributor(distributor);
        System.out.println(name + " se ha desuscrito de: " + distributor);
    }

    public Set<String> getSubscribedDistributors() {
        return UserManager.getInstance().getCurrentUserSubscriptions();
    }

    public boolean isSubscribedTo(String distributor) {
        return UserManager.getInstance().isCurrentUserSubscribedTo(distributor);
    }

    public boolean canViewMovie(Movie movie) {
        return movie.getDistributor() == null || UserManager.getInstance().isCurrentUserSubscribedTo(movie.getDistributor());
    }
}