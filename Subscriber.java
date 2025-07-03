public interface Subscriber {
    void notify(Movie movie);
    void addReview(Movie movie, String review);
}
