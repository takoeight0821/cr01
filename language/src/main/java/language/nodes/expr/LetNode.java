package language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.nodes.stmt.SimpleDecl;

@NodeInfo(shortName = "let")
public final class LetNode extends ExprNode {
    @Child private SimpleDecl declNode;
    @Child private ExprNode bodyNode;

    public LetNode(SimpleDecl declNode, ExprNode bodyNode) {
        this.declNode = declNode;
        this.bodyNode = bodyNode;
    }

    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        declNode.executeVoid(frame);
        return bodyNode.executeGeneric(frame);
    }
}
