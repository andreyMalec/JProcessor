package samples;

import javax.inject.Singleton;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class DatabaseModule {
    @Provides
    @Singleton
    public DatabaseConnection connection() {
        return new DatabaseConnection("127.0.0.1");
    }
}
