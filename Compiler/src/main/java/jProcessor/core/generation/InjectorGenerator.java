package jProcessor.core.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Provider;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import jProcessor.core.ProviderNotFoundException;
import jProcessor.core.data.InjectorData;
import jProcessor.core.data.ModuleData;
import jProcessor.core.data.ProviderData;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.checkProvider;
import static jProcessor.util.Ext.firstOrNull;
import static jProcessor.util.Ext.map;

public class InjectorGenerator extends BaseGenerator<Void> {
    private final InjectorData injectorData;
    private final List<ProviderData> providers = new ArrayList<>();
    private final Set<String> providersNames = new HashSet<>();
    private final String packageName;

    public InjectorGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, InjectorData injectorData) {
        super(log, filer, roundEnv);
        this.injectorData = injectorData;
        Set<ProviderData> set = new HashSet<>();
        injectorData.modulesData.forEach(it -> set.addAll(it.providers));
        providers.addAll(set);
        providers.sort((Comparator.comparingInt(o -> o.params.size())));
        packageName = getPackage(injectorData.modulesData.get(0).packageName);
    }

    @Override
    public Void generate() {
        createFile(createInjector(), packageName);
        return null;
    }

    private MethodSpec addInjectMethod(TypeName targetType, List<Element> fields) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT).addModifiers(Modifier.PUBLIC)
                .addParameter(targetType, INSTANCE).returns(void.class);

        for (Element field : fields) {
            TypeMirror providerType = checkProvider(
                    firstOrNull(providers, it -> it.returnType.equals(field.asType())),
                    field.asType()
            ).returnType;
            String returnType = simpleName(name(providerType));
            String paramName = Character.toLowerCase(returnType.charAt(0)) + returnType
                    .substring(1) + PROVIDER;
            builder.addStatement(INSTANCE + ".$L = $L." + GET + "()", field.getSimpleName(),
                    paramName
            );
        }

        MethodSpec generated = builder.build();
        log.note("addInjectMethod: ", generated);
        return generated;
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (ModuleData moduleData : injectorData.modulesData)
            builder.addStatement("$L $L = new $L()", moduleData.type, providerName(moduleData.type),
                    moduleData.type
            );

        for (ProviderData provider : providers) {
            StringBuilder sb = new StringBuilder();
            sb.append("this.$L = new $L.$L($L");
            String providerName = providerName(provider.returnType);

            for (int i = 0; i < provider.params.size(); i++) {
                if (i == 0)
                    sb.append(", ");
                sb.append("$L");
                if (i + 1 < provider.params.size())
                    sb.append(", ");
            }

            Object[] params = map(provider.params, it -> it.getSimpleName().toString() + PROVIDER)
                    .toArray();
            for (Object p : params) {
                String param = (String) p;
                if (!providersNames.contains(param))
                    throw new ProviderNotFoundException(
                            Character.toUpperCase(param.charAt(0)) + param.substring(1)
                                    .replace(PROVIDER, ""));
            }
            ModuleData module = Objects.requireNonNull(firstOrNull(injectorData.modulesData,
                    it -> simpleName(it.type).equals(provider.factory.split("_")[0])
            ));
            TypeName moduleType = module.type;

            Object[] values = new Object[provider.params.size() + 4];
            values[0] = providerName;
            values[1] = module.packageName;
            values[2] = provider.factory;
            values[3] = providerName(moduleType);

            System.arraycopy(params, 0, values, 4, params.length);

            sb.append(")");
            builder.addStatement(sb.toString(), values);
        }

        MethodSpec generated = builder.build();
        log.note("addConstructor: ", generated);
        return generated;
    }

    private MethodSpec addGetMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(packageName, INJECTOR));

        builder.addStatement("return $L", "injector");

        MethodSpec generated = builder.build();
        log.note("addGetMethod: ", generated);
        return generated;
    }

    private TypeSpec createInjector() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(INJECTOR)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (ProviderData provider : providers) {
            TypeName returnType = name(provider.returnType);
            String providerName = providerName(returnType);
            builder.addField(ParameterizedTypeName.get(ClassName.get(Provider.class), returnType),
                    providerName, Modifier.PRIVATE
            );
            providersNames.add(providerName);
        }

        builder.addField(FieldSpec
                .builder(ClassName.get(packageName, INJECTOR), "injector", Modifier.PRIVATE,
                        Modifier.FINAL, Modifier.STATIC
                ).initializer("new $L()", INJECTOR).build());

        builder.addMethod(addConstructor());

        for (int i = 0; i < injectorData.targetTypes.size(); i++)
            builder.addMethod(addInjectMethod(name(injectorData.targetTypes.get(i)),
                    injectorData.fields.get(i)
            ));

        builder.addMethod(addGetMethod());

        TypeSpec generated = builder.build();
        log.note("createProvider: ", generated);
        return generated;
    }
}
