package tests.testPackage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import jProcessor.Module;
import jProcessor.Provides;
import tests.B;
import tests.C;
import tests.Cat;
import tests.D;

@Module
public class SecondAppModule {
    private static final Cat cat = new Cat(2.3, 5, "Vasya");

    @Provides
    @Singleton
    public Cat provideCat() {
        return cat;
    }

    @Provides
    @Singleton
    public List<List<Cat>> provideCats() {
        List<Cat> cats = new ArrayList<>();
        List<List<Cat>> cats2 = new ArrayList<>();
        cats.add(cat);
        cats2.add(cats);
        return cats2;
    }

    @Provides
    @Singleton
    public List<Set<Cat>> provideCatsSet() {
        Set<Cat> cats = new HashSet<>();
        List<Set<Cat>> cats2 = new ArrayList<>();
        cats.add(cat);
        cats2.add(cats);
        return cats2;
    }

    @Provides
    @Singleton
    public D d(B bob, C c) {
        return new D(bob, c);
    }

    @Provides
    public B b() {
        return new B(10);
    }
}
