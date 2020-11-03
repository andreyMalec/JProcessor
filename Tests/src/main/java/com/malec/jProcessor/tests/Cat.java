package com.malec.jProcessor.tests;

import com.malec.jProcessor.processor.annotation.Arg;
import com.malec.jProcessor.processor.annotation.Default;

@Default(args = {@Arg(name = "weight", value = "2.2"), @Arg(name = "isDomestic", value = "true")})
public class Cat {
    public double weight;
    public int age;
    public String name;
    public boolean isDomestic;
}
