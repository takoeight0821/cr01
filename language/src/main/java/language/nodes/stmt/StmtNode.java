package language.nodes.stmt;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(description = "The abstract base node for all statements")
public abstract class StmtNode extends Node {
    public abstract void executeVoid(VirtualFrame frame);
}
