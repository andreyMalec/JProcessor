package jProcessor.core.generation;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import jProcessor.core.ProviderVisitor;
import jProcessor.core.data.Binding;
import jProcessor.core.data.ModuleData;
import jProcessor.core.data.Parameter;
import jProcessor.util.Logger;

public class ModuleGenerator extends BaseGenerator<ModuleData> {
    private final TypeName moduleType;
    private final String packageName;
    private final List<ExecutableElement> providers;

    public ModuleGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, Element module) {
        super(log, filer, roundEnv);
        moduleType = name(module.asType());
        Element e = module.getEnclosingElement();
        while (e.getKind() != ElementKind.PACKAGE)
            e = e.getEnclosingElement();
        packageName = e.toString() + "." + fieldName(moduleType);
        providers = findProviders(module);
    }

    @Override
    public ModuleData generate() {
        List<Binding> providerDataList = new ArrayList<>();
        for (ExecutableElement provider : providers) {
            List<? extends VariableElement> parameters = provider.getParameters();
            TypeSpec p = createProvider(provider, parameters);
            createFile(p, packageName);
            providerDataList.add(new Binding(
                    new Parameter(providerName(provider.getReturnType()), name(provider.getReturnType())),
                    parameters.stream().map(it -> new Parameter(providerName(it.asType()), name(it.asType())))
                            .collect(ImmutableList.toImmutableList()),
                    parameters.size(),
                    p.name
            ));
        }
        return new ModuleData(new Parameter(simpleName(moduleType), moduleType),
                ImmutableList.copyOf(providerDataList)
        );
    }

    private MethodSpec addGetMethod(
            String providerName, TypeName providerType, List<? extends VariableElement> params, boolean isLazy
    ) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class).returns(providerType);

        StringBuilder sb = new StringBuilder();
        if (isLazy) {
            sb.append("$L = ");
            appendProviderGet(sb, params.size());

            Object[] values = new Object[params.size() + 3];
            values[0] = fieldName(providerType);
            values[1] = providerName;
            values[2] = MODULE;
            System.arraycopy(params.toArray(), 0, values, 3, params.size());

            builder.addCode(CodeBlock.builder().beginControlFlow("if ($L == null)", fieldName(providerType))
                    .addStatement(sb.toString(), values).endControlFlow().build());

            builder.addStatement("return $L", fieldName(providerType));
        } else {
            sb.append("return ");
            appendProviderGet(sb, params.size());

            Object[] values = new Object[params.size() + 2];
            values[0] = providerName;
            values[1] = MODULE;
            System.arraycopy(params.toArray(), 0, values, 2, params.size());

            builder.addStatement(sb.toString(), values);
        }

        return builder.build();
    }

    private void appendProviderGet(StringBuilder sb, int paramsCount) {
        sb.append("$L($L");
        for (int i = 0; i < paramsCount; i++) {
            if (i == 0)
                sb.append(", ");
            sb.append("$L");
            sb.append(PROVIDER);
            sb.append(".");
            sb.append(GET);
            sb.append("()");
            if (i + 1 < paramsCount)
                sb.append(", ");
        }
        sb.append(")");
    }

    private MethodSpec addProvideMethod(
            String providerName, TypeName providerType, List<? extends VariableElement> params
    ) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(providerName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).addParameter(moduleType, INSTANCE)
                .returns(providerType);

        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(INSTANCE);
        sb.append(".$L(");

        int i = 0;
        for (VariableElement param : params) {
            builder.addParameter(name(param.asType()), param.getSimpleName().toString());
            sb.append("$L");
            if (++i < params.size())
                sb.append(", ");
        }

        sb.append(")");

        Object[] values = new Object[params.size() + 1];
        values[0] = providerName;
        System.arraycopy(params.toArray(), 0, values, 1, params.size());

        builder.addStatement(sb.toString(), values);
        return builder.build();
    }

    private MethodSpec addConstructor(List<? extends VariableElement> params) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(moduleType, MODULE).addStatement("this." + MODULE + " = " + MODULE);

        for (VariableElement param : params) {
            String paramProviderName = param.getSimpleName().toString() + PROVIDER;
            builder.addParameter(
                    ParameterizedTypeName.get(ClassName.get(Provider.class), name(param.asType())),
                    paramProviderName
            );
            builder.addStatement("this.$L = $L", paramProviderName, paramProviderName);
        }

        return builder.build();
    }

    private TypeSpec createProvider(ExecutableElement provider, List<? extends VariableElement> params) {
        TypeName providerType = name(provider.getReturnType());
        String providerName = provider.getSimpleName().toString();
        String moduleName = simpleName(moduleType);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(moduleName + "_" + simpleName(providerType) + PROVIDER);
        builder.addModifiers(Modifier.PUBLIC);
        builder.addField(moduleType, MODULE, Modifier.PRIVATE, Modifier.FINAL);

        boolean isLazy = provider.getAnnotation(Singleton.class) != null;
        if (isLazy) {
            builder.addField(FieldSpec
                    .builder(providerType, fieldName(providerType), Modifier.PRIVATE, Modifier.STATIC)
                    .build());
        }

        for (VariableElement param : params)
            builder.addField(ParameterizedTypeName.get(ClassName.get(Provider.class), name(param.asType())),
                    param.getSimpleName().toString() + PROVIDER,
                    Modifier.PRIVATE,
                    Modifier.FINAL
            );

        builder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Provider.class), providerType));

        builder.addMethod(addConstructor(params));
        builder.addMethod(addGetMethod(providerName, providerType, params, isLazy));
        builder.addMethod(addProvideMethod(providerName, providerType, params));

        return builder.build();
    }

    private List<ExecutableElement> findProviders(Element module) {
        ProviderVisitor v = new ProviderVisitor();
        module.accept(v, null);

        return v.providers();
    }
}
