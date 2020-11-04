package com.malec.jProcessor.core.generation;

import java.io.PrintWriter;
import java.util.Collections;

public class PrintWriterPrinter implements TabbedPrinter {
    private PrintWriter out;

    private int tabIndex = 0;

    public PrintWriterPrinter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void print(String text) {
        out.print(text);
    }

    @Override
    public void print(String... text) {
        for (String s : text)
            out.print(s);
    }

    @Override
    public void tabbedPrint(String text) {
        if (tabIndex > 0)
            out.print(repeat("    ", tabIndex));
        out.print(text);
    }

    @Override
    public void tabbedPrint(String... text) {
        if (tabIndex > 0)
            out.print(repeat("    ", tabIndex));

        for (String s : text)
            out.print(s);
    }

    @Override
    public void newLine() {
        out.println();
    }

    @Override
    public void incTab() {
        tabIndex++;
    }

    @Override
    public void decTab() {
        tabIndex--;
    }

    private String repeat(String src, int count) {
        return String.join("", Collections.nCopies(count, src));
    }
}
