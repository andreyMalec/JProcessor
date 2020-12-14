package jProcessor.core.generation;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Comparator;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import jProcessor.core.ProviderNotFoundException;
import jProcessor.core.data.Binding;
import jProcessor.core.data.BindingRequest;
import jProcessor.core.data.Injection;
import jProcessor.core.data.Parameter;
import jProcessor.core.data.TargetKind;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.appendCommaSeparated;
import static jProcessor.util.Ext.copyOf;
import static jProcessor.util.GuavaCollectors.toImmutableSet;

public class InjectorGenerator extends BaseGenerator {
    private Injection injection;
    private ImmutableSet<String> providersTypes;
    private String packageName;

    public InjectorGenerator(Logger log, Filer filer) {
        super(log, filer);
    }

    public void generate(Injection injection) {
        initInjection(injection);
        createFile(createInjector(), packageName);
    }

    private void initInjection(Injection injection) {
        this.injection = injection;
        providersTypes = injection.bindings.stream().map(it -> it.provider.type.toString())
                .collect(toImmutableSet());

        packageName = injection.modules.stream().map(this::getPackage)
                .min((Comparator.comparingInt(it -> it.split("\\.").length))).orElse("");
    }

    private MethodSpec addInjectMethod(BindingRequest request) {
        log.note("InjectorGenerator:     Adding " + INJECT + "(" + request.targetType + ") method");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT).addModifiers(Modifier.PUBLIC)
                .addParameter(request.targetType, INSTANCE).returns(void.class);

        for (Parameter field : request.parameters)
            builder.addStatement(INSTANCE + ".$L = $L." + GET + "()", field.name, providerName(field.type));

        return builder.build();
    }

    private MethodSpec addInjectConstructorMethod(BindingRequest request) {
        log.note("InjectorGenerator:     Adding " + GET + simpleName(request.targetType) + " method");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET + simpleName(request.targetType))
                .addModifiers(Modifier.PUBLIC).returns(request.targetType);

        boolean isLazy = request.targetKind == TargetKind.SINGLETON_CONSTRUCTOR;
        StringBuilder sb = new StringBuilder();
        if (isLazy)
            sb.append("$L = new $L(");
        else
            sb.append("return new $L(");

        appendCommaSeparated(sb, "$L." + GET + "()", request.parameters.size());
        sb.append(")");

        if (isLazy) {
            Object[] values = copyOf(request.parameters.stream().map(it -> providerName(it.type)).toArray(),
                    fieldName(request.targetType),
                    simpleName(request.targetType)
            );

            builder.addCode(CodeBlock.builder()
                    .beginControlFlow("if ($L == null)", fieldName(request.targetType))
                    .addStatement(sb.toString(), values).endControlFlow().build());

            builder.addStatement("return $L", fieldName(request.targetType));
        } else {
            Object[] values = copyOf(request.parameters.stream().map(it -> providerName(it.type)).toArray(),
                    simpleName(request.targetType)
            );

            builder.addStatement(sb.toString(), values);
        }

        return builder.build();
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (TypeName module : injection.modules)
            builder.addStatement("$L $L = new $L()", module, providerName(module), module);

        for (Binding binding : injection.bindings) {
            checkProviderFor(binding);

            TypeName module = (TypeName) injection.modules.stream()
                    .filter(it -> simpleName(it).equals(binding.factory.split("_")[0])).toArray()[0];

            Object[] values = copyOf(binding.provider.parameters.stream().map(it -> it.name).toArray(),
                    providerName(binding.provider.type),
                    getPackage(module) + "." + fieldName(module),
                    binding.factory,
                    providerName(module)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("this.$L = new $L.");
            appendProviderCall(sb, "$L", binding.provider.parameters.size());

            builder.addStatement(sb.toString(), values);
        }

        return builder.build();
    }

    private MethodSpec addGetMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(ClassName.get(packageName, INJECTOR));

        builder.addStatement("return $L", fieldName(INJECTOR));

        return builder.build();
    }

    private TypeSpec createInjector() {
        log.note("InjectorGenerator: Creating injector");
        TypeSpec.Builder builder = TypeSpec.classBuilder(INJECTOR)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Binding binding : injection.bindings)
            builder.addField(
                    provider(binding.provider.type),
                    providerName(binding.provider.type),
                    Modifier.PRIVATE
            );

        builder.addField(FieldSpec.builder(ClassName.get(packageName, INJECTOR), fieldName(INJECTOR))
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("new $L()", INJECTOR).build());

        builder.addMethod(addConstructor());

        for (BindingRequest request : injection.requests)
            if (request.targetKind == TargetKind.CONSTRUCTOR ||
                    request.targetKind == TargetKind.SINGLETON_CONSTRUCTOR) {
                if (request.targetKind == TargetKind.SINGLETON_CONSTRUCTOR)
                    builder.addField(request.targetType, fieldName(request.targetType), Modifier.PRIVATE);
                builder.addMethod(addInjectConstructorMethod(request));
            } else
                builder.addMethod(addInjectMethod(request));

        builder.addMethod(addGetMethod());

        return builder.build();
    }

    private void checkProviderFor(Binding binding) {
        for (Object paramType : binding.provider.parameters.stream().map(it -> it.type.toString()).toArray())
            if (!providersTypes.contains(paramType))
                throw new ProviderNotFoundException(paramType);
    }
}
