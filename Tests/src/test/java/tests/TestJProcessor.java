package tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class TestJProcessor {
    @Test
    public void testCircularDependency() {
        A a = new A();
        B b = new B();
        Injector.get().inject(a);
        Injector.get().inject(b);

        assertNull(a.b.a);
        assertNull(b.a.b);
    }

    @Test
    public void testDependency() {
        DependencyConsumer dcOne = new DependencyConsumer();
        DependencyConsumer dcTwo = new DependencyConsumer();

        assertNotEquals(dcOne.cat, dcTwo.cat);
        assertEquals(dcOne.cat.name, dcTwo.cat.name);
        assertEquals(dcOne.dependency, dcTwo.dependency);
    }
}
