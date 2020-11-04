package com.malec.jProcessor.processor;

import com.malec.jProcessor.processor.annotation.Data;

@Data
public class Person {
    int age;
    String name;
    String surname;
    String passport;
    String gender;

    public int test() {
        return 9;
    }
}
