package jProcessor.core.generation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import jProcessor.core.data.Binding;
import jProcessor.core.data.Parameter;
import jProcessor.core.data.Provider;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.append;
import static jProcessor.util.Ext.copyOf;

public class ProviderGenerator extends BaseGenerator {
    private Provider provider;
    private Object[] providerParamsNames;
    private TypeName moduleType;
    private String packageName;

    public ProviderGenerator(Logger log, Filer filer) {
        super(log, filer);
    }

    public Binding generate(Provider provider) {
        initProvider(provider);

        TypeSpec providerGenerated = createProvider();
        createFile(providerGenerated, packageName);
        return new Binding(provider, providerGenerated.name);
    }

    private void initProvider(Provider provider) {
        this.provider = provider;
        providerParamsNames = provider.parameters.stream().map(it -> it.name).toArray();

        moduleType = provider.module;
        packageName = getPackage(moduleType) + "." + fieldName(moduleType);
    }

    private MethodSpec addGetMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class).returns(provider.type);

        StringBuilder sb = new StringBuilder();
        if (provider.isLazy)
            sb.append("$L = ");
        else
            sb.append("return ");

        appendProviderCall(sb, "$L." + GET + "()", provider.parameters.size());

        if (provider.isLazy) {
            Object[] values = copyOf(providerParamsNames, fieldName(provider.type), provider.name, MODULE);

            builder.addCode(CodeBlock.builder().beginControlFlow("if ($L == null)", fieldName(provider.type))
                    .addStatement(sb.toString(), values).endControlFlow().build());

            builder.addStatement("return $L", fieldName(provider.type));
        } else {
            Object[] values = copyOf(providerParamsNames, provider.name, MODULE);

            builder.addStatement(sb.toString(), values);
        }

        return builder.build();
    }

    private MethodSpec addProvideMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(provider.name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).addParameter(moduleType, INSTANCE)
                .returns(provider.type);

        StringBuilder sb = new StringBuilder();
        append(sb, "return ", INSTANCE, ".$L(");

        int i = 0;
        for (Parameter param : provider.parameters) {
            builder.addParameter(param.type, param.name);
            sb.append("$L");
            if (++i < provider.parameters.size())
                sb.append(", ");
        }

        sb.append(")");

        Object[] values = copyOf(providerParamsNames, provider.name);

        builder.addStatement(sb.toString(), values);
        return builder.build();
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(moduleType, MODULE).addStatement("this." + MODULE + " = " + MODULE);

        for (Parameter param : provider.parameters) {
            String paramProviderName = providerName(param.type);
            builder.addParameter(provider(param.type), paramProviderName);
            builder.addStatement("this.$L = $L", paramProviderName, paramProviderName);
        }

        return builder.build();
    }

    private TypeSpec createProvider() {
        log.note("ProviderGenerator: Creating provider for " + provider.type);
        String moduleName = simpleName(moduleType);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(moduleName + "_" + simpleName(provider.type) + PROVIDER)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(moduleType, MODULE, Modifier.PRIVATE, Modifier.FINAL);

        if (provider.isLazy)
            builder.addField(FieldSpec.builder(provider.type, fieldName(provider.type))
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC).build());

        for (Parameter param : provider.parameters)
            builder.addField(provider(param.type), providerName(param.type), Modifier.PRIVATE, Modifier.FINAL);

        builder.addSuperinterface(provider(provider.type));

        builder.addMethod(addConstructor()).addMethod(addGetMethod()).addMethod(addProvideMethod());

        return builder.build();
    }
}
