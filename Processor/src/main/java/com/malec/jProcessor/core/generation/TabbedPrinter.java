package com.malec.jProcessor.core.generation;

public interface TabbedPrinter extends Printer {
    void tabbedPrint(String text);

    void tabbedPrint(String... text);

    void incTab();

    void decTab();
}
