package tests;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class A {
    @Inject
    public D d;

    @Inject
    public List<List<Cat>> cats;

    @Inject
    public List<Set<Cat>> catsSet;

    public A() {
        Injector.get().inject(this);
    }
}
