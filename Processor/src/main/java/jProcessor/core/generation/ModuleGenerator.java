package jProcessor.core.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Provider;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import jProcessor.Provides;
import jProcessor.core.data.ModuleData;
import jProcessor.core.data.ProviderData;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.map;
import static jProcessor.util.Ext.toList;

public class ModuleGenerator extends BaseGenerator<ModuleData> {
    private final TypeName moduleType;
    private final String packageName;
    private final List<ProviderData> providers = new ArrayList<>();

    public ModuleGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, Element module) {
        super(log, filer, roundEnv);
        this.moduleType = name(module.asType());
        Element e = module.getEnclosingElement();
        while (e.getKind() != ElementKind.PACKAGE)
            e = e.getEnclosingElement();
        packageName = e.toString();
    }

    @Override
    public ModuleData generate() {
        for (ExecutableElement provider : findProviders(roundEnv)) {
            TypeSpec p = createProvider(provider, provider.getParameters());
            createFile(p, packageName);
            providers.add(new ProviderData(provider.getSimpleName(), provider.getReturnType(),
                    provider.getParameters(), p.name
            ));
        }
        return new ModuleData(moduleType.toString(), packageName, moduleType, providers);
    }

    private MethodSpec addGetMethod(String providerName, TypeName providerType, List<? extends VariableElement> params) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(GET).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class).returns(providerType);

        StringBuilder sb = new StringBuilder();
        sb.append("return $L($L");

        for (int i = 0; i < params.size(); i++) {
            if (i == 0)
                sb.append(", ");
            sb.append("$L");
            sb.append(PROVIDER);
            sb.append(".");
            sb.append(GET);
            sb.append("()");
            if (i + 1 < params.size())
                sb.append(", ");
        }

        sb.append(")");

        Object[] values = new Object[params.size() + 2];
        values[0] = providerName;
        values[1] = MODULE;
        System.arraycopy(params.toArray(), 0, values, 2, params.size());

        builder.addStatement(sb.toString(), values);
        MethodSpec generated = builder.build();
        log.note("addGetMethod: ", generated);
        return generated;
    }

    private MethodSpec addProvideMethod(String providerName, TypeName providerType, List<? extends VariableElement> params) {
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
        MethodSpec generated = builder.build();
        log.note("addProvideMethod: ", generated);
        return generated;
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

        MethodSpec generated = builder.build();
        log.note("addConstructor: ", generated);
        return generated;
    }

    private TypeSpec createProvider(ExecutableElement provider, List<? extends VariableElement> params) {
        TypeName providerType = name(provider.getReturnType());
        String providerName = provider.getSimpleName().toString();
        String moduleName = simpleName(moduleType);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(moduleName + "_" + simpleName(providerType) + PROVIDER);
        builder.addModifiers(Modifier.PUBLIC);
        builder.addField(moduleType, MODULE, Modifier.PRIVATE, Modifier.FINAL);

        for (VariableElement param : params)
            builder.addField(
                    ParameterizedTypeName.get(ClassName.get(Provider.class), name(param.asType())),
                    param.getSimpleName().toString() + PROVIDER, Modifier.PRIVATE, Modifier.FINAL
            );

        builder.addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(Provider.class), providerType));

        builder.addMethod(addConstructor(params));
        builder.addMethod(addGetMethod(providerName, providerType, params));
        builder.addMethod(addProvideMethod(providerName, providerType, params));

        TypeSpec generated = builder.build();
        log.note("createProvider: ", generated);
        return generated;
    }

    private List<ExecutableElement> findProviders(RoundEnvironment roundEnv) {
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Provides.class);

        return map(toList(annotated), it -> (ExecutableElement) it);
    }
}
