package tests;

import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import jProcessor.Module;
import jProcessor.Provides;

@Module
public class AppModule {
    @Provides
    @Singleton
    public Dependency dependency(List<Cat> cats) {
        return new Dependency(cats);
    }

    @Provides
    public Cat cat() {
        return new Cat(2.6, 5, "V");
    }

    @Provides
    @Singleton
    public List<Cat> cats() {
        return Collections.singletonList(new Cat(3.6, 7, "S"));
    }

    @Provides
    @Singleton
    public A a() {
        return new A();
    }

    @Provides
    @Singleton
    public B b() {
        return new B();
    }
}
