package com.malec.jProcessor.processor.generation;

import com.malec.jProcessor.processor.Argument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultConstructorGenerator implements CodeGenerator {
    private final String className;
    private final String packageName;
    private final String simpleClassName;
    private final String constructorSimpleClassName;
    private final List<Argument> args;
    private TabbedPrinter p;

    public DefaultConstructorGenerator(TabbedPrinter printer, String className, List<Argument> args) {
        this.p = printer;

        this.className = className;
        this.packageName = getPackage(className);
        this.simpleClassName = getSimpleClassName(className);

        String constructorClassName = className + "Constructor";
        this.constructorSimpleClassName = getSimpleClassName(constructorClassName);

        this.args = new ArrayList<>(args);
    }

    @Override
    public void generate() {
        appendPackage(packageName);
        appendImport(className);

        openClass(constructorSimpleClassName);
        appendMethod(simpleClassName, "byDefault", args);
        closeClass();
    }

    protected void appendPackage(String packageName) {
        if (packageName == null)
            return;

        p.tabbedPrint("package ", packageName, ";");
        p.newLine();
        p.newLine();
    }

    protected void appendImport(String... className) {
        for (String name : className) {
            p.tabbedPrint("import ", name, ";");
            p.newLine();
        }
        p.newLine();
    }

    protected void openClass(String simpleClassName) {
        p.tabbedPrint("public final class ", simpleClassName, " {");
        p.newLine();
        p.newLine();
        p.incTab();
    }

    protected void closeClass() {
        p.decTab();
        p.newLine();
        p.tabbedPrint("}");
        p.newLine();
    }

    protected void appendMethod(String returnType, String name, List<Argument> args) {
        p.tabbedPrint("public static ", returnType, " ", name, "(");
        final int[] i = {0};
        List<Argument> requiredArguments = args.stream().filter(it -> it.value == null)
                .collect(Collectors.toList());
        requiredArguments.forEach(it -> {
            p.print(it.type, " ", it.name);
            if (++i[0] < requiredArguments.size())
                p.print(", ");
        });
        p.print(") {");
        p.newLine();
        p.incTab();

        p.tabbedPrint(simpleClassName, " object = new ", simpleClassName, "();");
        p.newLine();

        requiredArguments.forEach(it -> {
            p.tabbedPrint("object.", it.name, " = ", it.name, ";");
            p.newLine();
        });

        List<Argument> defaultArguments = args.stream().filter(it -> it.value != null)
                .collect(Collectors.toList());
        defaultArguments.forEach(it -> {
            p.tabbedPrint("object.", it.name, " = ", stringArgument(it.value), ";");
            p.newLine();
        });

        p.newLine();
        p.tabbedPrint("return object;");
        p.newLine();
        p.decTab();
        p.tabbedPrint("}");
    }

    private String stringArgument(String value) {
        String decorator = "";
        if (isString(value))
            decorator = "\"";
        return decorator + value + decorator;
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

    private String getPackage(String className) {
        String packageName = null;
        int lastDot = lastDoteIndex(className);
        if (lastDot > 0)
            packageName = className.substring(0, lastDot);

        return packageName;
    }

    private String getSimpleClassName(String className) {
        return className.substring(lastDoteIndex(className) + 1);
    }

    private int lastDoteIndex(String className) {
        return className.lastIndexOf('.');
    }
}
