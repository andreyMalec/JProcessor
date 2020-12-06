package com.malec.jProcessor.tests;

import org.junit.Test;

import tests.A;
import tests.DependencyConsumer;
import tests.Injector;

import static org.junit.Assert.assertEquals;

public class TestJProcessor {
    @Test
    public void testProvides() {
        DependencyConsumer dc = new DependencyConsumer();
        A a = Injector.get().getA();
        Injector.get().inject(a);
        assertEquals(dc.dependency.cat, a.getCats().get(0).get(0));
        assertEquals(dc.d, a.d);
    }
}
