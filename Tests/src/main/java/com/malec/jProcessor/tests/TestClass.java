package com.malec.jProcessor.tests;

import com.malec.jProcessor.Arg;
import com.malec.jProcessor.Default;

@Default(args = {@Arg(name = "testString", value = "\"123\""), @Arg(name = "testString2", value = "abc"), @Arg(name = "testDouble", value = "321.4")})
public class TestClass {
    public int testInt, testInt2;
    public double testDouble;
    public String testString, testString2;
}
