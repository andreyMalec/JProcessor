package tests;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

public class A {
    @Inject
    public static D d;

    private List<List<Cat>> cats;

    private List<Set<Cat>> catsSet;

    @Inject
    @Singleton
    public A(List<List<Cat>> cats, List<Set<Cat>> catsSet) {
        this.cats = cats;
        this.catsSet = catsSet;
    }

    public List<List<Cat>> getCats() {
        return cats;
    }
}
