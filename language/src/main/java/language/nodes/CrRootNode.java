package language.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import language.CrLanguage;
import language.nodes.expr.ExprNode;

public class CrRootNode extends RootNode {
    @Child
    private ExprNode bodyNode;

    public CrRootNode(CrLanguage language, FrameDescriptor frameDescriptor, ExprNode bodyNode) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return bodyNode.executeGeneric(frame);
    }
}
