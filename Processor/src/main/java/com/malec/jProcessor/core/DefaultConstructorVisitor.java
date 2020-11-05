package com.malec.jProcessor.core;

import com.malec.jProcessor.core.ast.ClassAnalyzer;
import com.malec.jProcessor.core.ast.ClassGenerator;
import com.malec.jProcessor.core.ast.ClassTreeAdapter;
import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class DefaultConstructorVisitor extends ClassVisitor {
    private final JCClassDecl root;
    private boolean constructorCreated = false;

    public DefaultConstructorVisitor(JCClassDecl root, ClassTreeAdapter adapter, ClassGenerator generator, Logger logger) {
        super(adapter, generator, logger);
        this.root = root;
    }

    @Override
    public void visitClass(JCClassDecl tree) {

    }

    @Override
    public void visitMethodDef(JCMethodDecl tree) {
        super.visitMethodDef(tree);

        JCVariableDecl[] fields = ClassAnalyzer.findFields(root, logger);
        for (int i = 0; i < fields.length; i++)
            fields[i] = generator.generateParameter(fields[i]);

        if (tree.name.toString().equals("<init>") && !constructorCreated) {
            result = generator.generateConstructor(tree, fields);
            constructorCreated = true;
        }
    }
}