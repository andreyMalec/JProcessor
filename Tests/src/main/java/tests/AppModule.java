package tests;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class AppModule {
    @Provides
    public Cat provideCat() {
        return new Cat(2.3, 5, "Vasya");
    }

    @Provides
    public Dependency dependency(Cat cat) {
        return new Dependency(cat);
    }
}
