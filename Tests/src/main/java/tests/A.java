package tests;

import javax.inject.Inject;

public class A {
    @Inject
    public D d;

    @Inject
    public Cat cat;

    public A() {
        Injector.get().inject(this);
    }
}
