package language.nodes.stmt

import com.oracle.truffle.api.dsl.NodeChild
import com.oracle.truffle.api.dsl.NodeField
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.frame.FrameSlotKind
import com.oracle.truffle.api.frame.VirtualFrame
import language.nodes.expr.ExprNode

@NodeField(name = "slot", type = FrameSlot::class)
@NodeChild(value = "valueNode", type = ExprNode::class)
abstract class SimpleDeclNode : StmtNode() {
    protected abstract val slot: FrameSlot?
    @Specialization(guards = ["isLongOrIllegal(frame)"])
    fun declLong(frame: VirtualFrame, value: Long) {
        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Long)
        frame.setLong(slot, value)
    }

    @Specialization(replaces = ["declLong"])
    fun decl(frame: VirtualFrame, value: Any?) {
        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Object)
        frame.setObject(slot, value)
    }

    fun isLongOrIllegal(frame: VirtualFrame): Boolean {
        val kind = frame.frameDescriptor.getFrameSlotKind(slot)
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal
    }
}