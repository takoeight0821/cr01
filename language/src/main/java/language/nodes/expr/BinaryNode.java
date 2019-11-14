package language.nodes.expr;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class BinaryNode extends ExprNode {
}
