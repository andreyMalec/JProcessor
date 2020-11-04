package com.malec.jProcessor.core;

import com.google.auto.service.AutoService;
import com.malec.jProcessor.Data;
import com.malec.jProcessor.Default;
import com.malec.jProcessor.core.ast.ClassGenerator;
import com.malec.jProcessor.core.ast.ClassTreeAdapter;
import com.malec.jProcessor.core.generation.BaseLogger;
import com.malec.jProcessor.core.generation.Logger;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.malec.jProcessor.Default", "com.malec.jProcessor.Data"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private final Map<TypeElement, DefaultConstructorVisitor> visitors = new HashMap<>();
    private Trees trees;
    private TreeMaker maker;
    private Names names;
    private Logger logger;

    private DataVisitor dataVisitor;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        trees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment) env).getContext();
        maker = TreeMaker.instance(context);
        names = Names.instance(context);

        logger = new BaseLogger(env.getMessager());

        dataVisitor = new DataVisitor(new ClassTreeAdapter(logger),
                new ClassGenerator(maker, names), logger
        );
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty())
            return false;

        AnnotationHandler handler = new AnnotationHandler(roundEnv, logger);

        handler.handleClassAnnotation(Data.class, it -> {
            JCTree tree = (JCTree) trees.getTree(it);
            tree.accept(dataVisitor);
        });

        handler.handleClassAnnotation(Default.class, it -> {
            DefaultConstructorVisitor visitor = new DefaultConstructorVisitor(processingEnv,
                    (TypeElement) it
            );
            it.accept(visitor, null);
            try {
                visitor.generateCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return true;
    }
}
