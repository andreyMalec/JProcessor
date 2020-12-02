package jProcessor.core.generation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Provider;
import javax.lang.model.element.Modifier;

import jProcessor.core.ProviderNotFoundException;
import jProcessor.core.data.Binding;
import jProcessor.core.data.Injection;
import jProcessor.core.data.ModuleData;
import jProcessor.core.data.Parameter;
import jProcessor.util.Logger;
import jProcessor.util.Pair;

import static jProcessor.core.data.Binding.selfCheck;
import static jProcessor.util.Ext.findDuplicate;
import static jProcessor.util.Ext.firstOrNull;

public class InjectorGenerator extends BaseGenerator<Void> {
    private final Injection injection;

    private final ImmutableList<Binding> bindings;
    private final String packageName;

    public InjectorGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, Injection injection) {
        super(log, filer, roundEnv);
        this.injection = injection;
        bindings = injection.modulesDatas.stream().flatMap(it -> it.bindings.stream())
                .sorted((Comparator.comparingInt(it -> it.providerParams.size())))
                .collect(ImmutableList.toImmutableList());

        Binding firstDuplicate = findDuplicate(bindings);
        if (firstDuplicate != null) {
            throw new IllegalStateException(
                    firstDuplicate.provider.type + " is bound multiple times in " + firstDuplicate.factory.split("_")[0]);
        }

        List<String> modulesTypes = injection.modulesDatas.stream().map(it -> getPackage(it.module.type))
                .sorted((Comparator.comparingInt(String::length))).collect(Collectors.toList());
        packageName = modulesTypes.get(0);
    }

    @Override
    public Void generate() {
        createFile(createInjector(), packageName);
        return null;
    }

    private MethodSpec addInjectMethod(TypeName targetType, ImmutableList<Parameter> fields) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT).addModifiers(Modifier.PUBLIC)
                .addParameter(targetType, INSTANCE).returns(void.class);

        for (Parameter field : fields) {
            Parameter provider = selfCheck(firstOrNull(bindings,
                    it -> it.provider.type.toString().equals(field.type.toString())
            )).provider;
            builder.addStatement(INSTANCE + ".$L = $L." + GET + "()", field.name, provider.name);
        }

        MethodSpec generated = builder.build();
        //        log.note("addInjectMethod: ", generated);
        return generated;
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (ModuleData moduleData : injection.modulesDatas)
            builder.addStatement("$L $L = new $L()",
                    moduleData.module.type,
                    providerName(moduleData.module.name),
                    moduleData.module.type
            );

        ImmutableSet<String> providersTypes = bindings.stream().map(it -> it.provider.type.toString())
                .collect(ImmutableSet.toImmutableSet());
        for (Binding binding : bindings) {
            StringBuilder sb = new StringBuilder();
            sb.append("this.$L = new $L.$L($L");
            String providerName = binding.provider.name;

            for (int i = 0; i < binding.providerParamsCount; i++) {
                if (i == 0)
                    sb.append(", ");
                sb.append("$L");
                if (i + 1 < binding.providerParamsCount)
                    sb.append(", ");
            }

            Object[] params = binding.providerParams.stream().map(it -> it.name).toArray();
            ImmutableList<String> paramsTypes = binding.providerParams.stream().map(it -> it.type.toString())
                    .collect(ImmutableList.toImmutableList());
            for (String paramType : paramsTypes)
                if (!providersTypes.contains(paramType))
                    throw new ProviderNotFoundException(paramType);

            ModuleData moduleData = ModuleData.selfCheck(firstOrNull(injection.modulesDatas,
                    it -> it.module.name.equals(binding.factory.split("_")[0])
            ));
            TypeName moduleType = moduleData.module.type;

            Object[] values = new Object[binding.providerParamsCount + 4];
            values[0] = providerName;
            values[1] = getPackage(moduleType) + "." + fieldName(moduleType);
            values[2] = binding.factory;
            values[3] = providerName(moduleType);

            System.arraycopy(params, 0, values, 4, params.length);

            sb.append(")");
            builder.addStatement(sb.toString(), values);
        }

        MethodSpec generated = builder.build();
        //        log.note("addConstructor: ", generated);
        return generated;
    }

    private MethodSpec addGetMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(ClassName.get(packageName, INJECTOR));

        builder.addStatement("return $L", "injector");

        MethodSpec generated = builder.build();
        //        log.note("addGetMethod: ", generated);
        return generated;
    }

    private TypeSpec createInjector() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(INJECTOR)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Binding binding : bindings) {
            TypeName providerType = binding.provider.type;
            builder.addField(ParameterizedTypeName.get(ClassName.get(Provider.class), providerType),
                    binding.provider.name,
                    Modifier.PRIVATE
            );
        }

        builder.addField(FieldSpec.builder(ClassName.get(packageName, INJECTOR),
                "injector",
                Modifier.PRIVATE,
                Modifier.FINAL,
                Modifier.STATIC
        ).initializer("new $L()", INJECTOR).build());

        builder.addMethod(addConstructor());

        ImmutableList<Pair<TypeName, ImmutableList<Parameter>>> requests = injection.requests.stream()
                .map(it -> Pair.of(it.targetType, it.fields)).collect(ImmutableList.toImmutableList());

        for (Pair<TypeName, ImmutableList<Parameter>> request : requests)
            builder.addMethod(addInjectMethod(request.first, request.second));

        builder.addMethod(addGetMethod());

        TypeSpec generated = builder.build();
        //        log.note("createInjector: ", generated);
        return generated;
    }
}
