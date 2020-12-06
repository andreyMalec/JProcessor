package jProcessor.core.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;

import jProcessor.core.NameManager;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.appendCommaSeparated;

public abstract class BaseGenerator<T> implements NameManager {
    protected final Logger log;
    protected final Filer filer;

    public BaseGenerator(Logger log, Filer filer) {
        this.log = log;
        this.filer = filer;
    }

    protected static void appendProviderCall(StringBuilder sb, String argument, int argumentsCount) {
        sb.append("$L($L");
        if (argumentsCount > 0) {
            sb.append(", ");
            appendCommaSeparated(sb, argument, argumentsCount);
        }
        sb.append(")");
    }

    public abstract T generate();

    protected void createFile(TypeSpec module, String packageName) {
        JavaFile javaFile = JavaFile.builder(packageName, module).skipJavaLangImports(true)
                .addFileComment("Generated by jProcessor.").indent("     ").build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
