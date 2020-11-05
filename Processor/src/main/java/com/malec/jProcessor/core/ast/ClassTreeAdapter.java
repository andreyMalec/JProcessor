package com.malec.jProcessor.core.ast;

import com.malec.jProcessor.core.generation.Logger;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.util.List;

public class ClassTreeAdapter {
    protected final Logger logger;

    protected JCClassDecl hostTree;

    protected JCTree[] addedMembers;
    protected List<JCTree> oldMembers;

    protected int addedMembersSize = 0;

    protected boolean started = false;

    public ClassTreeAdapter(Logger logger) {
        this.logger = logger;
    }

    public void start(JCClassDecl tree) {
        if (started)
            throw new IllegalStateException(
                    "ClassTreeAdapter already started. Commit changes for continue");

        hostTree = tree;

        initMembers();

        started = true;
    }

    protected void initMembers() {
        oldMembers = hostTree.defs;

        int newSize = oldMembers.size() * 2;
        addedMembers = new JCTree[newSize];
    }

    public JCClassDecl commit() {
        if (!started)
            throw new IllegalStateException("Nothing to commit. ClassTreeAdapter not started");

        hostTree.defs = List.from(makeMembers());

        started = false;
        addedMembersSize = 0;

        return hostTree;
    }

    public void addMember(JCTree memberTree) {
        if (!started)
            throw new IllegalStateException("ClassTreeAdapter not started");

        addedMembers[addedMembersSize++] = memberTree;
    }

    public void replaceMember(JCTree oldMember, JCTree newMember) {
        if (!started)
            throw new IllegalStateException("ClassTreeAdapter not started");

        int index = oldMembers.indexOf(oldMember);
        JCTree[] old = oldMembers.toArray(new JCTree[0]);
        old[index] = newMember;
        oldMembers = List.from(old);
    }

    protected JCTree[] makeMembers() {
        int oldMembersSize = oldMembers.size();

        JCTree[] members = new JCTree[oldMembersSize + addedMembersSize];

        for (int i = 0; i < oldMembersSize; i++)
            members[i] = oldMembers.get(i);

        System.arraycopy(addedMembers, 0, members, oldMembersSize, addedMembersSize);

        return members;
    }
}
