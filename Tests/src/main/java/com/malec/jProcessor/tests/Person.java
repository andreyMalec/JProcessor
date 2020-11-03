package com.malec.jProcessor.tests;

import com.malec.jProcessor.processor.annotation.Any;
import com.malec.jProcessor.processor.annotation.Default;

@Default(args = {@Any(name = "name", value = "Vasya"), @Any(name = "surname", value = "Pupkin"), @Any(name = "gender", value = "male")})
public class Person {
    public String name;
    public String surname;
    public int age;
    public String gender;
    public String passport;
}
