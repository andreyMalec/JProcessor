package com.malec.jProcessor.processor;

import com.google.auto.service.AutoService;
import com.malec.jProcessor.processor.annotation.Data;
import com.malec.jProcessor.processor.annotation.Default;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
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
@SupportedAnnotationTypes({"com.malec.jProcessor.processor.annotation.Default", "com.malec.jProcessor.processor.annotation.Data"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private final Map<TypeElement, DefaultConstructorVisitor> visitors = new HashMap<>();
    private Trees trees;
    private TreeMaker maker;
    private Names names;
    private Messager messager;

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
            DefaultConstructorVisitor visitor = visitors.get(object);
            if (visitor == null) {
                visitor = new DefaultConstructorVisitor(processingEnv, object);
                visitors.put(object, visitor);
            }
            element.accept(visitor, null);
        }

        for (Element element : dataAnnotated) {
            if (element.getKind() == ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "Working on " + element.asType().toString() + "..."
                );

                JCTree tree = (JCTree) trees.getTree(element);
                tree.accept(new DataGenerator());
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

    public class DataGenerator extends TreeTranslator {
        @Override
        public void visitClassDef(JCClassDecl tree) {
            super.visitClassDef(tree);
            //log input tree
            //messager.printMessage(Diagnostic.Kind.NOTE, tree.toString());

            int size = tree.defs.size();
            int newSize = size * 3;
            JCTree[] membersWithNull = new JCTree[newSize];

            int j = 0;
            for (int i = 0; i < size; i++) {
                JCTree member = tree.defs.get(i);
                if (member instanceof JCVariableDecl) {
                    JCVariableDecl var = (JCVariableDecl) member;

                    JCVariableDecl field = maker
                            .VarDef(maker.Modifiers(Flags.PRIVATE), var.name, var.vartype, null);
                    JCVariableDecl setterField = maker
                            .VarDef(maker.Modifiers(Flags.PARAMETER), var.name, var.vartype, null);

                    membersWithNull[i] = field;
                    membersWithNull[size + j++] = createSetter(setterField);
                    membersWithNull[size + j++] = createGetter(setterField);
                } else
                    membersWithNull[i] = member;
            }

            JCTree[] members = new JCTree[size + j];
            System.arraycopy(membersWithNull, 0, members, 0, size + j);

            tree.defs = List.from(members);

            //log outout tree
            //messager.printMessage(Diagnostic.Kind.NOTE, tree.toString());

            result = tree;
        }

        private JCMethodDecl createSetter(JCVariableDecl node) {
            JCModifiers modifiers = maker.Modifiers(Flags.PUBLIC);
            String nodeName = node.name.toString();
            Name name = getName(
                    "set" + Character.toUpperCase(nodeName.charAt(0)) + nodeName.substring(1));
            JCExpression returnType = maker.TypeIdent(TypeTag.VOID);
            List<JCTypeParameter> types = List.nil();
            List<JCVariableDecl> parameters = List.of(node);
            List<JCExpression> throwz = List.nil();
            JCBlock methodBody = createBlockExec(
                    maker.Assign(maker.Select(maker.Ident(getName("this")), node.name),
                            maker.Ident(node.name)
                    ));
            return maker
                    .MethodDef(modifiers, name, returnType, types, parameters, throwz, methodBody,
                            null
                    );
        }

        private JCMethodDecl createGetter(JCVariableDecl node) {
            JCModifiers modifiers = maker.Modifiers(Flags.PUBLIC);
            String nodeName = node.name.toString();
            Name name = getName(
                    "get" + Character.toUpperCase(nodeName.charAt(0)) + nodeName.substring(1));
            JCExpression returnType = node.vartype;
            List<JCTypeParameter> types = List.nil();
            List<JCVariableDecl> parameters = List.nil();
            List<JCExpression> throwz = List.nil();
            JCBlock methodBody = createBlockReturn(maker.Ident(node.name));
            return maker
                    .MethodDef(modifiers, name, returnType, types, parameters, throwz, methodBody,
                            null
                    );
        }

        private JCBlock createBlockExec(JCExpression e) {
            return createBlock(maker.Exec(e));
        }

        private JCBlock createBlockReturn(JCExpression e) {
            return createBlock(maker.Return(e));
        }

        private JCBlock createBlock(JCStatement s) {
            return maker.Block(0, List.of(s));
        }

        private Name getName(String string) {
            return names.fromString(string);
        }
    }
}
