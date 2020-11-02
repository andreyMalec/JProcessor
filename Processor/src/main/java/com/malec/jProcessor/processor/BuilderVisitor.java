package com.malec.jProcessor.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner7;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class BuilderVisitor extends ElementScanner7<Void, Void> {
    private final Messager mLogger;

    private final Filer mFiler;

    private List<String> argsNames = new ArrayList<>();
    private List<String> argsTypes = new ArrayList<>();
    private String className;
    private Any[] args;

    public BuilderVisitor(ProcessingEnvironment env, TypeElement element) {
        super();
        mLogger = env.getMessager();
        mFiler = env.getFiler();
        className = element.getQualifiedName().toString();
    }

    @Override
    public Void visitVariable(VariableElement field, Void aVoid) {
        Default aDefault = field.getEnclosingElement().getAnnotation(Default.class);
        args = aDefault.args();

        argsNames.add(field.getSimpleName().toString());
        argsTypes.add(field.asType().toString());

        return super.visitVariable(field, aVoid);
    }

    public void generateCode() throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Constructor";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        mLogger.printMessage(Diagnostic.Kind.NOTE, "Analyze " + simpleClassName + "...");

        StringBuilder argsString = new StringBuilder();
        for (Any arg : args) {
            int i = argsNames.indexOf(arg.name());
            if (i >= 0) {
                argsNames.remove(i);
                argsTypes.remove(i);
            }
            argsString.append(arg.name());
            argsString.append(", ");
        }
        String argsStringFinal = argsString
                .replace(argsString.length() - 2, argsString.length(), "").toString();
        mLogger.printMessage(Diagnostic.Kind.NOTE,
                "Found [" + argsStringFinal + "](" + args.length + ") fields need to be filled"
        );

        mLogger.printMessage(Diagnostic.Kind.NOTE, "Creating " + builderSimpleClassName + "...");

        JavaFileObject builderFile = mFiler.createSourceFile(builderClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("import ");
            out.print(className);
            out.println(";");
            out.println();

            out.print("public final class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println();

            out.print("    public static ");
            out.print(simpleClassName);
            out.print(" byDefault(");
            final int[] i = {0};
            argsNames.forEach(e -> {
                out.print(argsTypes.get(i[0]));
                out.print(" ");
                out.print(e);
                if (++i[0] < argsNames.size())
                    out.print(", ");
            });
            out.println(") {");
            out.print("        ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.print("()");
            out.println(";");

            argsNames.forEach(it -> {
                out.print("        object.");
                out.print(it);
                out.print(" = ");
                out.print(it);
                out.println(";");
            });
            for (Any arg : args) {
                out.print("        object.");
                out.print(arg.name());
                out.print(" = ");
                boolean isString = isString(arg.value());
                if (isString)
                    out.print("\"");
                out.print(arg.value());
                if (isString)
                    out.print("\"");
                out.println(";");
            }

            out.println();
            out.println("        return object;");
            out.println("    }");

            out.println("}");
        }
    }

    private boolean isString(String value) {
        IntStream v = value.chars();
        String vL = value.toLowerCase();

        if (v.anyMatch(Character::isDigit))
            return false;

        if (vL.equals("true") || vL.equals("false"))
            return false;

        return true;
    }
}
