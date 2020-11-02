package com.malec.jProcessor.processor.generation;

public interface TabbedPrinter extends Printer {
    void tabbedPrint(String text);

    void tabbedPrint(String... text);

    void incTab();

    void decTab();
}
