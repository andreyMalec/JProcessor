package tests;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class AppModule {
    @Provides
    public Dependency dependency(Cat cat) {
        return new Dependency(cat);
    }

    @Provides
    public C c(B b) {
        return new C(b);
    }
}
