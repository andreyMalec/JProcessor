package tests;

import javax.inject.Inject;

public class DependencyConsumer {
    @Inject
    public Cat cat;

    @Inject
    public Dependency dependency;

    @Inject
    public D d;

    public DependencyConsumer() {
        Injector.get().inject(this);
    }
}
