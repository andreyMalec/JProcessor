package jProcessor.core.generation;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
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
import jProcessor.util.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static jProcessor.util.Ext.appendCall;
import static jProcessor.util.Ext.copyOf;
import static jProcessor.util.Ext.findDuplicate;
import static jProcessor.util.Ext.firstOrNull;

@SuppressWarnings("UnstableApiUsage")
public class InjectorGenerator extends BaseGenerator<Void> {
    private final Injection injection;
    private final ImmutableSet<String> providersTypes;
    private final String packageName;

    public InjectorGenerator(Logger log, Filer filer, Injection injection) {
        super(log, filer);
        this.injection = injection;
        providersTypes = injection.bindings.stream().map(it -> it.provider.type.toString())
                .collect(ImmutableSet.toImmutableSet());

        Binding firstDuplicate = findDuplicate(injection.bindings);
        if (firstDuplicate != null)
            throw new IllegalStateException(firstDuplicate.provider.type + " is bound multiple times in " +
                    firstDuplicate.factory.split("_")[0]);

        packageName = injection.modules.stream().map(this::getPackage)
                .min((Comparator.comparingInt(it -> it.split("\\.").length))).orElse("");
    }

    @Override
    public Void generate() {
        createFile(createInjector(), packageName);
        return null;
    }

    private MethodSpec addInjectMethod(BindingRequest request) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT).addModifiers(Modifier.PUBLIC)
                .addParameter(request.targetType, INSTANCE).returns(void.class);

        for (Parameter field : request.fields) {
            String providerName = checkNotNull(firstOrNull(
                    injection.bindings,
                    it -> it.provider.type.toString().equals(field.type.toString())
            )).provider.name;
            builder.addStatement(INSTANCE + ".$L = $L." + GET + "()", field.name, providerName);
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

            Object[] values = copyOf(
                    binding.providerParams.stream().map(it -> it.name).toArray(),
                    binding.provider.name,
                    getPackage(module) + "." + fieldName(module),
                    binding.factory,
                    providerName(module)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("this.$L = new $L.");
            appendCall(sb, "$L", binding.providerParams.size());

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
        TypeSpec.Builder builder = TypeSpec.classBuilder(INJECTOR)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Binding binding : injection.bindings)
            builder.addField(provider(binding.provider.type), binding.provider.name, Modifier.PRIVATE);

        builder.addField(FieldSpec.builder(ClassName.get(packageName, INJECTOR), fieldName(INJECTOR))
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("new $L()", INJECTOR).build());

        builder.addMethod(addConstructor());

        for (BindingRequest request : injection.requests)
            builder.addMethod(addInjectMethod(request));

        builder.addMethod(addGetMethod());

        return builder.build();
    }

    private void checkProviderFor(Binding binding) {
        for (Object paramType : binding.providerParams.stream().map(it -> it.type.toString()).toArray())
            if (!providersTypes.contains(paramType))
                throw new ProviderNotFoundException(paramType);
    }
}
