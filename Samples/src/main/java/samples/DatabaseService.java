package samples;

import javax.inject.Inject;
import javax.inject.Singleton;

public class DatabaseService {
    private final DatabaseConnection connection;

    @Inject
    @Singleton
    public DatabaseService(DatabaseConnection connection) {
        this.connection = connection;
    }

    public DatabaseConnection getConnection() {
        return connection;
    }
}
