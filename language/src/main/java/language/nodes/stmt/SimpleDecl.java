package language.nodes.stmt;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import language.nodes.expr.ExprNode;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeChild(value = "valueNode", type = ExprNode.class)
public abstract class SimpleDecl extends StmtNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isLongOrIllegal(frame)")
    protected void declLong(VirtualFrame frame, long value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);
        frame.setLong(getSlot(), value);
    }

    @Specialization(replaces = "declLong")
    protected void decl(VirtualFrame frame, Object value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        frame.setObject(getSlot(), value);
    }

    protected boolean isLongOrIllegal(VirtualFrame frame) {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
    }
}
