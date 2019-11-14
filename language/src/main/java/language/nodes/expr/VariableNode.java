package language.nodes.expr;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.*;
import language.nodes.expr.ExprNode;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class VariableNode extends ExprNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isLong(frame)")
    protected long readLong(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    protected boolean isLong(VirtualFrame frame) {
        return frame.getFrameDescriptor().getFrameSlotKind(getSlot()) == FrameSlotKind.Long;
    }
}
