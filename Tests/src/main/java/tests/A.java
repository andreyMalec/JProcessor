package tests;

import javax.inject.Inject;


public class A {
    @Inject
    public Dependency dependency;

    public A() {
        Injector.get().inject(this);
    }
}
