package jProcessor.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class BaseLogger implements Logger {
    private final Messager log;

    public BaseLogger(Messager log) {
        this.log = log;
    }

    @Override
    public void note(Object... text) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : text)
            sb.append(obj.toString());
        log.printMessage(Diagnostic.Kind.NOTE, sb.toString());
    }

    @Override
    public void error(Object... text) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : text)
            sb.append(obj.toString());
        log.printMessage(Diagnostic.Kind.ERROR, sb.toString());
    }
}
