import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String title;
    private String imageUrl;
    private String genre;
    private String year;
    private String description;
    private String distributor;
    private List<Review> reviews;

    public Movie(String title, String imageUrl, String genre, String year, String description, String distributor) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.genre = genre;
        this.year = year;
        this.description = description;
        this.distributor = distributor;
        this.reviews = new ArrayList<>();
    }

    public Movie(String title, String imageUrl, String genre, String year, String description) {
        this(title, imageUrl, genre, year, description, null);
    }

    public Movie(String title, String imageUrl) {
        this(title, imageUrl, null, null, null, null);
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public String getDistributor() {
        return distributor;
    }

    public void addReview(String comment, int rating) {
        addReview(comment, rating, "Usuario Anónimo");
    }
    
    public void addReview(String comment, int rating, String username) {
        if (rating >= 1 && rating <= 10) {
            reviews.add(new Review(comment, rating, username));
        }
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public int getReviewCount() {
        return reviews.size();
    }

    public static class Review {
        private String comment;
        private int rating;
        private String username;

        public Review(String comment, int rating, String username) {
            this.comment = comment;
            this.rating = rating;
            this.username = username;
        }

        public String getComment() {
            return comment;
        }

        public int getRating() {
            return rating;
        }
        
        public String getUsername() {
            return username;
        }
    }
}