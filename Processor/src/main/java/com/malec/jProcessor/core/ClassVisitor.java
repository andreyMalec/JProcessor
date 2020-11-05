package com.malec.jProcessor.core;

import com.malec.jProcessor.core.ast.ClassGenerator;
import com.malec.jProcessor.core.ast.ClassTreeAdapter;
import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

public abstract class ClassVisitor extends TreeTranslator {
    protected final ClassTreeAdapter adapter;
    protected final ClassGenerator generator;
    protected final Logger logger;

    protected ClassVisitor(ClassTreeAdapter adapter, ClassGenerator generator, Logger logger) {
        this.logger = logger;
        this.adapter = adapter;
        this.generator = generator;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        super.visitClassDef(tree);

        adapter.start(tree);

        visitClass(tree);

        result = adapter.commit();
    }

    protected abstract void visitClass(JCTree.JCClassDecl tree);
}

