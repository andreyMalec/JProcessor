package jProcessor.core.generation;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import jProcessor.core.data.Binding;
import jProcessor.core.data.Parameter;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.append;
import static jProcessor.util.Ext.appendCall;
import static jProcessor.util.Ext.copyOf;

public class ProviderGenerator extends BaseGenerator<Binding> {
    private final ImmutableList<Parameter> providerParams;
    private final Object[] providerParamsNames;
    private final TypeName providerType;
    private final String providerName;
    private final boolean isLazy;
    private final TypeName moduleType;
    private final String packageName;

    @SuppressWarnings("UnstableApiUsage")
    public ProviderGenerator(Logger log, Filer filer, ExecutableElement provider) {
        super(log, filer);
        providerParams = provider.getParameters().stream()
                .map(it -> new Parameter(providerName(it.asType()), name(it.asType())))
                .collect(ImmutableList.toImmutableList());
        providerParamsNames = providerParams.stream().map(it -> it.name).toArray();

        providerType = name(provider.getReturnType());
        providerName = provider.getSimpleName().toString();
        isLazy = provider.getAnnotation(Singleton.class) != null;

        Element module = provider.getEnclosingElement();
        moduleType = name(module.asType());
        Element e = module.getEnclosingElement();
        while (e.getKind() != ElementKind.PACKAGE)
            e = e.getEnclosingElement();
        packageName = e.toString() + "." + fieldName(moduleType);
    }

    @Override
    public Binding generate() {
        TypeSpec provider = createProvider();
        createFile(provider, packageName);
        return new Binding(new Parameter(providerName(providerType), providerType),
                providerParams,
                provider.name
        );
    }

    private MethodSpec addGetMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class).returns(providerType);

        StringBuilder sb = new StringBuilder();
        if (isLazy)
            sb.append("$L = ");
        else
            sb.append("return ");

        appendCall(sb, "$L." + GET + "()", providerParams.size());

        if (isLazy) {
            Object[] values = copyOf(providerParamsNames, fieldName(providerType), providerName, MODULE);

            builder.addCode(CodeBlock.builder().beginControlFlow("if ($L == null)", fieldName(providerType))
                    .addStatement(sb.toString(), values).endControlFlow().build());

            builder.addStatement("return $L", fieldName(providerType));
        } else {
            Object[] values = copyOf(providerParamsNames, providerName, MODULE);

            builder.addStatement(sb.toString(), values);
        }

        return builder.build();
    }

    private MethodSpec addProvideMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(providerName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).addParameter(moduleType, INSTANCE)
                .returns(providerType);

        StringBuilder sb = new StringBuilder();
        append(sb, "return ", INSTANCE, ".$L(");

        int i = 0;
        for (Parameter param : providerParams) {
            builder.addParameter(param.type, param.name);
            sb.append("$L");
            if (++i < providerParams.size())
                sb.append(", ");
        }

        sb.append(")");

        Object[] values = copyOf(providerParamsNames, providerName);

        builder.addStatement(sb.toString(), values);
        return builder.build();
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(moduleType, MODULE).addStatement("this." + MODULE + " = " + MODULE);

        for (Parameter param : providerParams) {
            String paramProviderName = providerName(param.type);
            builder.addParameter(provider(param.type), paramProviderName);
            builder.addStatement("this.$L = $L", paramProviderName, paramProviderName);
        }

        return builder.build();
    }

    private TypeSpec createProvider() {
        String moduleName = simpleName(moduleType);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(moduleName + "_" + simpleName(providerType) + PROVIDER)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(moduleType, MODULE, Modifier.PRIVATE, Modifier.FINAL);

        if (isLazy)
            builder.addField(FieldSpec.builder(providerType, fieldName(providerType))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC).build());

        for (Parameter param : providerParams)
            builder.addField(provider(param.type),
                    providerName(param.type),
                    Modifier.PRIVATE,
                    Modifier.FINAL
            );

        builder.addSuperinterface(provider(providerType));

        builder.addMethod(addConstructor()).addMethod(addGetMethod()).addMethod(addProvideMethod());

        return builder.build();
    }
}
