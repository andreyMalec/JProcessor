package com.malec.jProcessor.core.ast;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

public class ClassGenerator {
    protected final TreeMaker maker;
    protected final Names names;

    public ClassGenerator(TreeMaker maker, Names names) {
        this.maker = maker;
        this.names = names;
    }

    protected JCVariableDecl generateParameter(JCVariableDecl var) {
        return maker.VarDef(maker.Modifiers(Flags.PARAMETER), var.name, var.vartype, null);
    }

    public JCMethodDecl generateSetter(JCVariableDecl var) {
        JCVariableDecl parameter = generateParameter(var);

        JCModifiers modifiers = maker.Modifiers(Flags.PUBLIC);
        Name name = getName("set" + toFirstUpper(parameter.name.toString()));
        JCExpression returnType = maker.TypeIdent(TypeTag.VOID);
        JCBlock methodBody = execBlock(
                maker.Assign(maker.Select(maker.Ident(getName("this")), var.name),
                        maker.Ident(parameter.name)
                ));
        return maker
                .MethodDef(modifiers, name, returnType, List.nil(), List.of(parameter), List.nil(),
                        methodBody, null
                );
    }

    public JCMethodDecl generateGetter(JCVariableDecl var) {
        JCVariableDecl parameter = generateParameter(var);

        JCModifiers modifiers = maker.Modifiers(Flags.PUBLIC);
        Name name = getName("get" + toFirstUpper(parameter.name.toString()));
        JCBlock methodBody = returnBlock(maker.Ident(parameter.name));
        return maker
                .MethodDef(modifiers, name, parameter.vartype, List.nil(), List.nil(), List.nil(),
                        methodBody, null
                );
    }

    private JCBlock execBlock(JCExpression e) {
        return block(maker.Exec(e));
    }

    private JCBlock returnBlock(JCExpression e) {
        return block(maker.Return(e));
    }

    private JCBlock block(JCStatement s) {
        return maker.Block(0, List.of(s));
    }

    private Name getName(String string) {
        return names.fromString(string);
    }

    private String toFirstUpper(String src) {
        return Character.toUpperCase(src.charAt(0)) + src.substring(1);
    }
}
