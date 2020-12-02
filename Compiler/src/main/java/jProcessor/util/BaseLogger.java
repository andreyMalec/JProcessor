package jProcessor.util;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        if (text.length == 1 && text[0] instanceof RuntimeException) {
            RuntimeException t = (RuntimeException) text[0];
            StringWriter stackTraceWriter = new StringWriter();
            t.printStackTrace(new PrintWriter(stackTraceWriter));
            log.printMessage(Diagnostic.Kind.ERROR, stackTraceWriter.toString());
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object obj : text)
                sb.append(obj.toString());
            log.printMessage(Diagnostic.Kind.ERROR, sb.toString());
        }
    }
}
