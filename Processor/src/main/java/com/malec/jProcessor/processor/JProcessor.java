package com.malec.jProcessor.processor;

import com.google.auto.service.AutoService;
import com.malec.jProcessor.processor.annotation.Data;
import com.malec.jProcessor.processor.annotation.Default;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.malec.jProcessor.processor.annotation.Default"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private Trees trees;
    private TreeMaker maker;
    private Names names;
    private Messager messager;

    private final Map<TypeElement, DefaultConstructorVisitor> visitors = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        trees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment) env).getContext();
        maker = TreeMaker.instance(context);
        names = Names.instance(context);
        messager = env.getMessager();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> defaultAnnotated = roundEnv.getElementsAnnotatedWith(Default.class);
        Set<? extends Element> dataAnnotated = roundEnv.getElementsAnnotatedWith(Data.class);

        for (Element element : defaultAnnotated) {
            TypeElement object = (TypeElement) element;
//            DefaultConstructorVisitor visitor = visitors.get(object);
//            if (visitor == null) {
//                visitor = new DefaultConstructorVisitor(processingEnv, object);
//                visitors.put(object, visitor);
//            }
//            element.accept(visitor, null);
        }

        for (Element element : dataAnnotated) {
            if (element.getKind() == ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Working on " + element.asType().toString() + "...");

                JCTree tree = (JCTree) trees.getTree(element);
                tree.accept(new Simplifier());
            }
        }

        for (final DefaultConstructorVisitor visitor : visitors.values()) {
            try {
                visitor.generateCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public class Simplifier extends TreeTranslator {
        @Override
        public void visitVarDef(JCVariableDecl tree) {
            super.visitVarDef(tree);
            JCMethodDecl newNode = createSetter(tree);
            messager.printMessage(Diagnostic.Kind.NOTE, newNode.toString());
            result = newNode;
        }

        private JCMethodDecl createSetter(JCVariableDecl node) {
            JCModifiers modifiers = maker.Modifiers(Flags.PUBLIC);
            String nodeName = node.name.toString();
            Name name = getName("set" + Character.toUpperCase(nodeName.charAt(0)) + nodeName.substring(1));
            JCExpression returnType = maker.TypeIdent(TypeTag.INT);
            List<JCTypeParameter> types = List.nil();
            List<JCVariableDecl> parameters = makeParam(node.vartype.toString(), node.name);
            List<JCExpression> throwz = List.nil();
            JCBlock methodBody = makeBody(node.name);
            return maker.MethodDef(modifiers, name, returnType, types, parameters, throwz, methodBody, null);
        }

        private List<JCVariableDecl> makeParam(String type, Name name) {
            JCExpression paramType = maker.Ident(getName(type));

            JCVariableDecl paramDecl =  maker.VarDef(maker.Modifiers(Flags.PARAMETER), name, paramType, null);

            return List.of(paramDecl);
        }

        private JCBlock makeBody(Name name) {
            JCExpression printExpression = maker.Ident(getName("this"));
            printExpression = maker.Select(printExpression, name);
            printExpression = maker.Assign(printExpression, maker.Ident(name));

            JCStatement call = maker.Exec(printExpression);

            List<JCStatement> statements = List.of(maker.Return(maker.Literal(1)));

            return maker.Block(0, statements);
        }

        private Name getName(String string) {
            Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
            return Names.instance(context).fromString(string);
        }
    }
}
