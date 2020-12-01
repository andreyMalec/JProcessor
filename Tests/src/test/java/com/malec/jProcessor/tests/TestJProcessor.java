package com.malec.jProcessor.tests;

import org.junit.Test;

import tests.A;
import tests.DependencyConsumer;

import static org.junit.Assert.assertEquals;

public class TestJProcessor {
    @Test
    public void testProvides() {
        DependencyConsumer dc = new DependencyConsumer();
        A a = new A();
        assertEquals(dc.dependency.cat, a.cats.get(0).get(0));
    }
}
