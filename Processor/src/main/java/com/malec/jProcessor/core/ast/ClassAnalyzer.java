package com.malec.jProcessor.core.ast;

import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

import java.util.function.Predicate;

public class ClassAnalyzer {
    public static JCVariableDecl[] findFields(JCClassDecl source, Logger log) {
        JCTree[] filtered = filter(source.defs.toArray(new JCTree[0]),
                it -> it instanceof JCVariableDecl, null
        );

        JCVariableDecl[] fields = new JCVariableDecl[filtered.length];
        for (int i = 0; i < filtered.length; i++)
            fields[i] = (JCVariableDecl) filtered[i];
        return fields;
    }

    public static JCMethodDecl[] findMethods(JCClassDecl source, Logger log) {
        JCTree[] filtered = filter(source.defs.toArray(new JCTree[0]),
                it -> it instanceof JCMethodDecl, null
        );

        JCMethodDecl[] methods = new JCMethodDecl[filtered.length];
        for (int i = 0; i < filtered.length; i++)
            methods[i] = (JCMethodDecl) filtered[i];
        return methods;
    }

    public static JCTree[] filter(JCTree[] list, Predicate<JCTree> predicate, Logger log) {
        int i = 0;
        JCTree[] nMembers = new JCTree[list.length];
        for (JCTree member : list)
            if (predicate.test(member))
                nMembers[i++] = member;

        JCTree[] members = new JCTree[i];
        System.arraycopy(nMembers, 0, members, 0, i);

        return members;
    }
}
