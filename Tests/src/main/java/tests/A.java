package tests;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class A {
    @Inject
    public D d;

    private List<List<Cat>> cats;

    private List<Set<Cat>> catsSet;

    @Inject
    public A(List<List<Cat>> cats, List<Set<Cat>> catsSet) {
        this.cats = cats;
        this.catsSet = catsSet;
    }

    public List<List<Cat>> getCats() {
        return cats;
    }
}
