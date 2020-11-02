package com.malec.jProcessor.tests;

import com.malec.jProcessor.processor.Builder;

public class TestClass {
    public int testInt, testInt2;
    public String testString, testString2;

    @Builder(var3 = "123", var4 = "abc")
    public TestClass(int var1, int var2, String var3, String var4) {
        this.testInt = var1;
        this.testInt2 = var2;
        this.testString = var3;
        this.testString2 = var4;
    }
}
