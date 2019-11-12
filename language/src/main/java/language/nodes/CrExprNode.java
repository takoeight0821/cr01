package language.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

@TypeSystemReference(CrTypes.class)
@NodeInfo(description = "The abstract base node for all expressions")
public abstract class CrExprNode extends Node{
    public abstract Object executeGeneric(VirtualFrame frame);
}
