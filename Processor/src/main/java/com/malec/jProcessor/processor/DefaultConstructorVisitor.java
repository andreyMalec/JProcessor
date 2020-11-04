package com.malec.jProcessor.processor;

import com.malec.jProcessor.processor.annotation.Arg;
import com.malec.jProcessor.processor.annotation.Default;
import com.malec.jProcessor.processor.generation.DefaultConstructorGenerator;
import com.malec.jProcessor.processor.generation.PrintWriterPrinter;
import com.malec.jProcessor.processor.generation.TabbedPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner7;
import javax.tools.JavaFileObject;

public class DefaultConstructorVisitor extends ElementScanner7<Void, Void> {
    private final Messager messager;
    private final Filer mFiler;

    private final String className;

    private List<String> argsNames = new ArrayList<>();
    private List<String> argsTypes = new ArrayList<>();
    private Arg[] optional;

    public DefaultConstructorVisitor(ProcessingEnvironment env, TypeElement element) {
        super();
        messager = env.getMessager();
        mFiler = env.getFiler();
        className = element.getQualifiedName().toString();
    }

    @Override
    public Void visitVariable(VariableElement field, Void aVoid) {
        Default byDefault = field.getEnclosingElement().getAnnotation(Default.class);
        optional = byDefault.args();

        argsNames.add(field.getSimpleName().toString());
        argsTypes.add(field.asType().toString());

        return super.visitVariable(field, aVoid);
    }

    public void generateCode() throws IOException {
        String constructorClassName = className + "Constructor";
        JavaFileObject constructorFile = mFiler.createSourceFile(constructorClassName);

        List<Argument> args = Arrays.stream(optional)
                .map(it -> new Argument(null, it.name(), it.value())).collect(Collectors.toList());

        for (Argument arg : args) {
            int i = argsNames.indexOf(arg.name);
            if (i >= 0) {
                argsNames.remove(i);
                argsTypes.remove(i);
            }
        }
        for (int i = 0; i < argsNames.size(); i++)
            args.add(new Argument(argsTypes.get(i), argsNames.get(i), null));

        try (PrintWriter out = new PrintWriter(constructorFile.openWriter())) {
            TabbedPrinter printer = new PrintWriterPrinter(out);
            new DefaultConstructorGenerator(printer, className, args).generate();
        }
    }
}