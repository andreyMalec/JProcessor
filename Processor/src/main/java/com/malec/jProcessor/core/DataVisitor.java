package com.malec.jProcessor.core;

import com.malec.jProcessor.core.ast.ClassAnalyzer;
import com.malec.jProcessor.core.ast.ClassGenerator;
import com.malec.jProcessor.core.ast.ClassTreeAdapter;
import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeTranslator;

public class DataVisitor extends TreeTranslator {
    private final ClassTreeAdapter adapter;
    private final ClassGenerator generator;
    private final Logger logger;

    public DataVisitor(ClassTreeAdapter adapter, ClassGenerator generator, Logger logger) {
        this.logger = logger;
        this.adapter = adapter;
        this.generator = generator;
    }

    @Override
    public void visitClassDef(JCClassDecl tree) {
        super.visitClassDef(tree);

        adapter.start(tree);

        for (JCVariableDecl var : ClassAnalyzer.findFields(tree, logger)) {
            adapter.addMember(generator.generateSetter(var));
            adapter.addMember(generator.generateGetter(var));
        }

        result = adapter.commit();
    }
}
