package language.nodes.builder;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import language.nodes.expr.ExprNode;
import language.nodes.expr.LetNode;
import language.nodes.stmt.SimpleDeclNode;
import language.nodes.stmt.SimpleDeclNodeGen;

import java.util.LinkedList;

public class LetExprBuilder {
    private CrNodeFactory.LexicalScope lexicalScope;
    private LinkedList<SimpleDeclNode> simpleDeclNodes;
    private FrameDescriptor frameDescriptor;

    LetExprBuilder(CrNodeFactory.LexicalScope lexicalScope) {
        this.lexicalScope = lexicalScope;
    }

    LetExprBuilder addSimpleDecl(String name, ExprNode value) {
        FrameSlot frameSlot = frameDescriptor.addFrameSlot(name, FrameSlotKind.Illegal);
        lexicalScope.locals.put(name, frameSlot);
        simpleDeclNodes.push(SimpleDeclNodeGen.create(value, frameSlot));
        return this;
    }

    LetNode buildLetNode(ExprNode bodyNode) {
        return new LetNode(simpleDeclNodes.toArray(new SimpleDeclNode[0]), bodyNode);
    }
}
