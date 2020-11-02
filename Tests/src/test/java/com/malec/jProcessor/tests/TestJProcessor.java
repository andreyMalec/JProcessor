package com.malec.jProcessor.tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJProcessor {
    @Test
    public void test() {
        TestClass test = TestClassConstructor.byDefault(0, 1);

        assertEquals(0, test.testInt);
        assertEquals(1, test.testInt2);

        assertEquals("123", test.testString);
        assertEquals("abc", test.testString2);

        assertEquals(321.4, test.testDouble, 0.1);
    }
}
