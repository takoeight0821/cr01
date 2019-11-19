package language.nodes.expr

import com.oracle.truffle.api.dsl.NodeField
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.frame.FrameSlotKind
import com.oracle.truffle.api.frame.FrameUtil
import com.oracle.truffle.api.frame.VirtualFrame

@NodeField(name = "slot", type = FrameSlot::class)
abstract class VariableNode : ExprNode() {
    protected abstract val slot: FrameSlot
    @Specialization(guards = ["isLong(frame)"])
    fun readLong(frame: VirtualFrame): Long = FrameUtil.getLongSafe(frame, slot)

    @Specialization(replaces = ["readLong"])
    fun read(frame: VirtualFrame): Any = FrameUtil.getObjectSafe(frame, slot)

    fun isLong(frame: VirtualFrame): Boolean = frame.frameDescriptor.getFrameSlotKind(slot) == FrameSlotKind.Long
}