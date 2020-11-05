package com.malec.jProcessor.core;

import com.malec.jProcessor.core.ast.ClassAnalyzer;
import com.malec.jProcessor.core.ast.ClassGenerator;
import com.malec.jProcessor.core.ast.ClassTreeAdapter;
import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class DataVisitor extends ClassVisitor {
    public DataVisitor(ClassTreeAdapter adapter, ClassGenerator generator, Logger logger) {
        super(adapter, generator, logger);
    }

    @Override
    public void visitClass(JCClassDecl tree) {
        for (JCVariableDecl var : ClassAnalyzer.findFields(tree, logger)) {
            adapter.addMember(generator.generateSetter(var));
            adapter.addMember(generator.generateGetter(var));
        }
    }
}
