package language.nodes;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.RootNode;
import language.CrLanguage;
import language.nodes.expr.ExprNode;

public class CrRootNode extends RootNode {
    @Child
    private ExprNode bodyNode;
    private final MaterializedFrame capturedEnv;

    public CrRootNode(CrLanguage language, FrameDescriptor frameDescriptor, ExprNode bodyNode, MaterializedFrame capturedEnv) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
        this.capturedEnv = capturedEnv;
    }

    public CrRootNode(CrLanguage language, FrameDescriptor frameDescriptor, ExprNode bodyNode) {
        this(language, frameDescriptor, bodyNode, null);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        // TODO: extract to Node object
        if (capturedEnv != null) {
            capturedEnv.getFrameDescriptor().getSlots().forEach((slot) -> {
                switch (capturedEnv.getFrameDescriptor().getFrameSlotKind(slot)) {
                    case Long: {
                        long value = FrameUtil.getLongSafe(capturedEnv, slot);
                        frame.getFrameDescriptor().setFrameSlotKind(slot, FrameSlotKind.Long);
                        frame.setLong(slot, value);
                        break;
                    }
                    case Object: {
                        Object value = FrameUtil.getObjectSafe(capturedEnv, slot);
                        frame.getFrameDescriptor().setFrameSlotKind(slot, FrameSlotKind.Object);
                        frame.setObject(slot, value);
                        break;
                    }
                }
            });
        }

        return bodyNode.executeGeneric(frame);
    }
}
