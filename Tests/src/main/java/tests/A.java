package tests;

import javax.inject.Inject;

public class A {
    @Inject
    public D d;

    public A() {
        Injector.get().inject(this);
    }
}
