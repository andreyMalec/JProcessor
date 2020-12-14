package samples;

public class DatabaseConnection {
    private final String url;

    public DatabaseConnection(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
