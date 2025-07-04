import java.util.Set;

public interface Subscriber {
    void notify(Movie movie);
    void addReview(Movie movie, String review);
    void addReview(Movie movie, String comment, int rating);
    void subscribeToDistributor(String distributor);
    void unsubscribeFromDistributor(String distributor);
    Set<String> getSubscribedDistributors();
    boolean canViewMovie(Movie movie);
}
