package com.malec.jProcessor.core.generation;

public interface Printer {
    void print(String text);

    void print(String... text);

    void newLine();
}
