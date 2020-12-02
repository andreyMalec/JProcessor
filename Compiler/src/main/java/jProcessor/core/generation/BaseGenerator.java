package jProcessor.core.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;

import jProcessor.core.NameManager;
import jProcessor.util.Logger;

public abstract class BaseGenerator<T> implements NameManager {
    protected final Logger log;
    protected final Filer filer;
    protected final RoundEnvironment roundEnv;

    public BaseGenerator(Logger log, Filer filer, RoundEnvironment roundEnv) {
        this.log = log;
        this.filer = filer;
        this.roundEnv = roundEnv;
    }

    public abstract T generate();

    protected void createFile(TypeSpec module, String packageName) {
        JavaFile javaFile = JavaFile.builder(packageName, module).skipJavaLangImports(true)
                .addFileComment("Generated by jProcessor.").indent("     ").build();
        try {
            log.note("createFile", javaFile);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
