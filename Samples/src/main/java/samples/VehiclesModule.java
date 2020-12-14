package samples;

import javax.inject.Singleton;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class VehiclesModule {
    @Provides
    public Engine engine() {
        return new Engine();
    }

    @Provides
    @Singleton
    public Brand brand() {
        return new Brand("brandName");
    }
}
