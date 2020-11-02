package com.malec.jProcessor.tests;

import com.malec.jProcessor.processor.annotation.Any;
import com.malec.jProcessor.processor.annotation.Default;

@Default(args = {@Any(name = "weight", value = "2.2"), @Any(name = "isDomestic", value = "true")})
public class Cat {
    public double weight;
    public int age;
    public String name;
    public boolean isDomestic;
}
