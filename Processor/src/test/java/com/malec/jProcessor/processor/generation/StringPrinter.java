package com.malec.jProcessor.processor.generation;

import java.util.Collections;

class StringPrinter implements TabbedPrinter {
    private final StringBuilder sb = new StringBuilder();

    private int tabIndex = 0;

    @Override
    public void tabbedPrint(String text) {
        if (tabIndex > 0)
            sb.append(repeat("    ", tabIndex));
        sb.append(text);
    }

    @Override
    public void tabbedPrint(String... text) {
        if (tabIndex > 0)
            sb.append(repeat("    ", tabIndex));

        for (String s : text)
            sb.append(s);
    }

    @Override
    public void incTab() {
        tabIndex++;
    }

    @Override
    public void decTab() {
        tabIndex--;
    }

    @Override
    public void print(String text) {
        sb.append(text);
    }

    @Override
    public void print(String... text) {
        for (String s : text)
            sb.append(s);
    }

    @Override
    public void newLine() {
        sb.append(System.lineSeparator());
    }

    public String get() {
        return sb.toString();
    }

    private String repeat(String src, int count) {
        return String.join("", Collections.nCopies(count, src));
    }
}