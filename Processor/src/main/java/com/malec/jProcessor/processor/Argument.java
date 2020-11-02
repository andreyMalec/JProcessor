package com.malec.jProcessor.processor;

public class Argument {
    public String type, name, value;

    public Argument(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Argument{" + "type='" + type + '\'' + ", name='" + name + '\'' + ", value='" + value + '\'' + '}';
    }
}
