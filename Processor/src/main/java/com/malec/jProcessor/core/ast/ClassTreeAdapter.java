package com.malec.jProcessor.core.ast;

import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;

public class ClassTreeAdapter {
    protected final Logger logger;

    protected JCClassDecl hostTree;

    protected JCTree[] addedMembers;
    protected List<JCTree> oldMembers;

    protected int addedMembersSize = 0;

    public ClassTreeAdapter(Logger logger) {
        this.logger = logger;
    }

    public void start(JCClassDecl tree) {
        hostTree = tree;

        initMembers();
    }

    protected void initMembers() {
        oldMembers = hostTree.defs;

        int newSize = oldMembers.size() * 2;
        addedMembers = new JCTree[newSize];
    }

    public JCClassDecl commit() {
        hostTree.defs = List.from(makeMembers());

        return hostTree;
    }

    public void addMember(JCTree memberTree) {
        if (memberTree instanceof JCVariableDecl)
            addField((JCVariableDecl) memberTree);
        else if (memberTree instanceof JCMethodDecl)
            addMethod((JCMethodDecl) memberTree);
        else
            throw new IllegalArgumentException("Unsupported member class " + memberTree);
    }

    protected JCTree[] makeMembers() {
        int oldMembersSize = oldMembers.size();

        JCTree[] members = new JCTree[oldMembersSize + addedMembersSize];

        for (int i = 0; i < oldMembersSize; i++)
            members[i] = oldMembers.get(i);

        System.arraycopy(addedMembers, 0, members, oldMembersSize, addedMembersSize);

        return members;
    }

    protected void addField(JCVariableDecl var) {
        addedMembers[addedMembersSize++] = var;
    }

    protected void addMethod(JCMethodDecl method) {
        addedMembers[addedMembersSize++] = method;
    }
}
