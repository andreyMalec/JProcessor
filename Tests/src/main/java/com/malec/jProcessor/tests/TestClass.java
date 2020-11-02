package com.malec.jProcessor.tests;

import com.malec.jProcessor.processor.Any;
import com.malec.jProcessor.processor.Default;

@Default(args = {@Any(name = "testString", value = "\"123\""), @Any(name = "testString2", value = "abc"), @Any(name = "testDouble", value = "321.4")})
public class TestClass {
    public int testInt, testInt2;
    public double testDouble;
    public String testString, testString2;
}
