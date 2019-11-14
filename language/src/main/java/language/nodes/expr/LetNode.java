package language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.nodes.stmt.SimpleDecl;

@NodeInfo(shortName = "let")
public final class LetNode extends ExprNode {
    @Children private final SimpleDecl[] declNodes;
    @Child private ExprNode bodyNode;

    public LetNode(SimpleDecl[] declNodes, ExprNode bodyNode) {
        this.declNodes = declNodes;
        this.bodyNode = bodyNode;
    }

    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        for ( var declNode : declNodes) {
            declNode.executeVoid(frame);
        }
        return bodyNode.executeGeneric(frame);
    }
}
