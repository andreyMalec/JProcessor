package com.malec.jProcessor.core.generation;

import com.malec.jProcessor.core.Argument;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestDefaultConstructorGenerator {
    @Test
    public void testGenerate() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("int", "testInt", null));
        args.add(new Argument("double", "testDouble", null));
        args.add(new Argument(null, "testString", "qwerty"));

        StringPrinter sp = new StringPrinter();

        new DefaultConstructorGenerator(sp, "test.class.Name", args).generate();

        String result = sp.get();

        assertTrue(result.contains("package test.class;"));

        assertTrue(result.contains("import test.class.Name;"));

        assertTrue(result.contains("public final class NameConstructor {"));
        assertTrue(result.contains(
                "    public static Name byDefault(int testInt, double testDouble) {"));
        assertTrue(result.contains("        Name object = new Name();"));
        assertTrue(result.contains("        object.testInt = testInt;"));
        assertTrue(result.contains("        object.testDouble = testDouble;"));
        assertTrue(result.contains("        object.testString = \"qwerty\";"));

        assertTrue(result.contains("        return object;"));
        assertTrue(result.contains("    }"));
        assertTrue(result.contains("}"));
    }
}
