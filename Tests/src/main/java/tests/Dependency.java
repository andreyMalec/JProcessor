package tests;

import java.util.List;

public class Dependency extends BaseDependency {
    public List<Cat> cats;

    public Dependency(List<Cat> cats) {
        this.cats = cats;
    }
}
