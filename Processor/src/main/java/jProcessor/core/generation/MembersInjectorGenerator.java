package jProcessor.core.generation;

import com.squareup.javapoet.ClassName;
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
import javax.lang.model.element.VariableElement;

import jProcessor.core.MembersInjector;
import jProcessor.util.Logger;

import static jProcessor.util.Ext.map;

public class MembersInjectorGenerator extends BaseGenerator<Void> {
    private final TypeName targetType;
    private final String targetName;
    private final List<VariableElement> fields;

    public MembersInjectorGenerator(Logger log, Filer filer, RoundEnvironment roundEnv, Element target, List<Element> fields) {
        super(log, filer, roundEnv);
        this.targetType = name(target.asType());
        this.targetName = target.getSimpleName().toString();
        this.fields = map(fields, it -> (VariableElement) it);
    }

    @Override
    public Void generate() {
        createFile(createMembersInjector(), targetName);
        return null;
    }

    private MethodSpec addInjectMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INJECT_MEMBERS)
                .addModifiers(Modifier.PUBLIC).addParameter(targetType, INSTANCE)
                .addAnnotation(Override.class).returns(void.class);

        for (VariableElement field : fields) {
            String paramName = field.getSimpleName().toString();
            String paramProviderName = paramName + PROVIDER;
            builder.addStatement(INSTANCE + ".$L = $L." + GET + "()", paramName, paramProviderName);
        }

        MethodSpec generated = builder.build();
        log.note("addInjectMethod: ", generated);
        return generated;
    }

    private MethodSpec addConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        for (VariableElement field : fields) {
            String paramProviderName = field.getSimpleName().toString() + PROVIDER;
            builder.addParameter(
                    ParameterizedTypeName.get(ClassName.get(Provider.class), name(field.asType())),
                    paramProviderName
            );
            builder.addStatement("this.$L = $L", paramProviderName, paramProviderName);
        }

        MethodSpec generated = builder.build();
        log.note("addConstructor: ", generated);
        return generated;
    }

    private TypeSpec createMembersInjector() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(targetName + "_" + MEMBERS_INJECTOR);

        for (VariableElement field : fields)
            builder.addField(
                    ParameterizedTypeName.get(ClassName.get(Provider.class), name(field.asType())),
                    field.getSimpleName().toString() + PROVIDER, Modifier.PRIVATE, Modifier.FINAL
            );

        builder.addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(MembersInjector.class), targetType));

        builder.addMethod(addConstructor());
        builder.addMethod(addInjectMethod());

        TypeSpec generated = builder.build();
        log.note("createProvider: ", generated);
        return generated;
    }
}
