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
    private LexicalScope lexicalScope;
    private LinkedList<SimpleDeclNode> simpleDeclNodes = new LinkedList<>();
    private FrameDescriptor frameDescriptor;

    LetExprBuilder(LexicalScope lexicalScope, FrameDescriptor frameDescriptor) {
        this.lexicalScope = lexicalScope;
        this.frameDescriptor = frameDescriptor;
    }

    public void addSimpleDecl(String name, ExprNode value) {
        FrameSlot frameSlot = frameDescriptor.addFrameSlot(new Object(), FrameSlotKind.Illegal);
        lexicalScope.locals.put(name, frameSlot);
        simpleDeclNodes.push(SimpleDeclNodeGen.create(value, frameSlot));
    }

    LetNode buildLetNode(ExprNode bodyNode) {
        return new LetNode(simpleDeclNodes.toArray(new SimpleDeclNode[0]), bodyNode);
    }
}
