package tests;

import javax.inject.Inject;

public class BaseDependency {
    @Inject
    public Cat cat;

    protected BaseDependency() {
        Injector.get().inject(this);
    }
}
