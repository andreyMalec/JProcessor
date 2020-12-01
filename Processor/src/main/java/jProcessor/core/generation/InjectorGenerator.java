package jProcessor.core.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Provider;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import jProcessor.core.data.InjectorData;
import jProcessor.core.data.ProviderData;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.filter;
import static jProcessor.util.Ext.map;

public class InjectorGenerator extends BaseGenerator<Void> {
    private final InjectorData injectorData;

    public InjectorGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, InjectorData injectorData) {
        super(log, filer, roundEnv);
        this.injectorData = injectorData;
    }

    @Override
    public Void generate() {
        createFile(createInjector(), injectorData.moduleData.packageName);
        return null;
    }

    private MethodSpec addInjectMethod(TypeName targetType, List<Element> fields) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT).addModifiers(Modifier.PUBLIC)
                .addParameter(targetType, INSTANCE).returns(void.class);

        for (Element field : fields) {
            ProviderData provider = filter(injectorData.moduleData.providers,
                    it -> it.returnType.equals(field.asType())
            ).get(0);
            String returnType = simpleName(name(provider.returnType));
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

        builder.addStatement("$L module = new $L()", injectorData.moduleData.type,
                injectorData.moduleData.type
        );

        for (ProviderData provider : injectorData.moduleData.providers) {
            StringBuilder sb = new StringBuilder();
            sb.append("this.$L = new $L(module");
            String returnType = simpleName(name(provider.returnType));
            String paramName = Character.toLowerCase(returnType.charAt(0)) + returnType
                    .substring(1) + PROVIDER;

            for (int i = 0; i < provider.params.size(); i++) {
                if (i == 0)
                    sb.append(", ");
                sb.append("$L");
                if (i + 1 < provider.params.size())
                    sb.append(", ");
            }

            Object[] fs = map(provider.params, it -> it.getSimpleName().toString() + PROVIDER)
                    .toArray();
            Object[] values = new Object[provider.params.size() + 2];
            values[0] = paramName;
            values[1] = provider.factory;
            System.arraycopy(fs, 0, values, 2, fs.length);

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
                .returns(ClassName.get(injectorData.moduleData.packageName, INJECTOR));

        builder.addStatement("return $L", "injector");

        MethodSpec generated = builder.build();
        log.note("addGetMethod: ", generated);
        return generated;
    }

    private TypeSpec createInjector() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(INJECTOR)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (ProviderData provider : injectorData.moduleData.providers) {
            TypeName returnType = name(provider.returnType);
            String simpleReturnType = simpleName(name(provider.returnType));
            String providerName = Character
                    .toLowerCase(simpleReturnType.charAt(0)) + simpleReturnType
                    .substring(1) + PROVIDER;
            builder.addField(ParameterizedTypeName.get(ClassName.get(Provider.class), returnType),
                    providerName, Modifier.PRIVATE
            );
        }

        builder.addField(FieldSpec
                .builder(ClassName.get(injectorData.moduleData.packageName, INJECTOR), "injector", Modifier.PRIVATE,
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
