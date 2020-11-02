package com.malec.jProcessor.processor.generation;

public interface Printer {
    void print(String text);

    void print(String... text);

    void newLine();
}
