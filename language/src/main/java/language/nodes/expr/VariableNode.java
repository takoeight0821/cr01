package language.nodes.expr;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class VariableNode extends ExprNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isLong(frame)")
    long readLong(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    @Specialization(replaces = "readLong")
    Object read(VirtualFrame frame) {
        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    boolean isLong(VirtualFrame frame) {
        return frame.getFrameDescriptor().getFrameSlotKind(getSlot()) == FrameSlotKind.Long;
    }
}
