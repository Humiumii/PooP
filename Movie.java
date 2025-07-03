public class Movie {
    private String title;
    private String imageUrl; // URL o ruta de la imagen

    public Movie(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}