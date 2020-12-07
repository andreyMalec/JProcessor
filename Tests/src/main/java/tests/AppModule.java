package tests;

import javax.inject.Singleton;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class AppModule {
    @Provides
    @Singleton
    public Dependency dependency(Cat cat) {
        return new Dependency(cat);
    }

    @Provides
    public static C c(B b) {
        return new C(b);
    }
}
