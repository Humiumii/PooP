public class Movie {
    private String title;
    private String imageUrl; // URL o ruta de la imagen
    private String genre;    // Nuevo campo
    private String year;     // Nuevo campo
    private String description; // Nuevo campo

    // Constructor actualizado
    public Movie(String title, String imageUrl, String genre, String year, String description) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.genre = genre;
        this.year = year;
        this.description = description;
    }

    // Constructor anterior para compatibilidad
    public Movie(String title, String imageUrl) {
        this(title, imageUrl, null, null, null);
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
}