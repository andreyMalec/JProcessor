package com.malec.jProcessor.processor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.malec.jProcessor.processor.Builder"})
public class JProcessor extends AbstractProcessor {
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv
                    .getElementsAnnotatedWith(annotation);

            Element currentElement = (Element) annotatedElements.toArray()[0];

            List<String> paramsType = ((ExecutableType) currentElement.asType()).getParameterTypes()
                    .stream().map(TypeMirror::toString).collect(Collectors.toList());

            List<Pair<String>> requiredParams = new ArrayList<>();
            List<Pair<String>> params = new ArrayList<>();
            Builder b = currentElement.getAnnotation(Builder.class);

            boolean t = false;
            try {
                if (b.var1().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(0), "var1"));
                else
                    params.add(new Pair<>(paramsType.get(0), b.var1()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var2().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(1), "var2"));
                else
                    params.add(new Pair<>(paramsType.get(1), b.var2()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var3().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(2), "var3"));
                else
                    params.add(new Pair<>(paramsType.get(2), b.var3()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var4().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(3), "var4"));
                else
                    params.add(new Pair<>(paramsType.get(3), b.var4()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var5().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(4), "var5"));
                else
                    params.add(new Pair<>(paramsType.get(4), b.var5()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var6().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(5), "var6"));
                else
                    params.add(new Pair<>(paramsType.get(5), b.var6()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                if (b.var7().isEmpty())
                    requiredParams.add(new Pair<>(paramsType.get(6), "var7"));
                else
                    params.add(new Pair<>(paramsType.get(6), b.var7()));
            } catch (IndexOutOfBoundsException ignored) {}

            try {
                writeBuilderFile(
                        ((TypeElement) currentElement.getEnclosingElement()).getQualifiedName()
                                .toString(), requiredParams, params, String.valueOf(t));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void writeBuilderFile(String className, List<Pair<String>> requiredParams, List<Pair<String>> params, String test) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

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

            //build
            out.print("    public static ");
            out.print(simpleClassName);
            out.print(" build(");
            final int[] i = {0};
            requiredParams.forEach(e -> {
                out.print(e.getFirst());
                out.print(" ");
                out.print(e.getSecond());
                if (++i[0] < requiredParams.size())
                    out.print(", ");
            });
            out.println(") {");
            out.print("        return new ");
            out.print(simpleClassName);
            out.print("(");
            requiredParams.forEach(e -> {
                out.print(e.getSecond());
                out.print(", ");
            });
            i[0] = 0;
            params.forEach(e -> {
                out.print("\"");
                out.print(e.getSecond());
                out.print("\"");
                if (i[0] != params.size() - 1)
                    out.print(", ");
                i[0]++;
            });
            out.print(");");
            out.println();
            out.println("    }");

            out.println("}");
        }
    }
}
