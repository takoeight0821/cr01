package language.nodes

import com.oracle.truffle.api.frame.*
import com.oracle.truffle.api.nodes.RootNode
import language.CrLanguage
import language.nodes.expr.ExprNode

class CrRootNode constructor(
    language: CrLanguage?,
    frameDescriptor: FrameDescriptor?,
    @field:Child private var bodyNode: ExprNode,
    private val capturedEnv: MaterializedFrame? = null
) : RootNode(language, frameDescriptor) {
    override fun execute(frame: VirtualFrame): Any {
        // TODO: extract to Node object
        capturedEnv?.frameDescriptor?.slots?.forEach { slot: FrameSlot? ->
            when (capturedEnv.frameDescriptor.getFrameSlotKind(slot)) {
                FrameSlotKind.Long -> {
                    val value = FrameUtil.getLongSafe(capturedEnv, slot)
                    frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Long)
                    frame.setLong(slot, value)
                }
                FrameSlotKind.Object -> {
                    val value = FrameUtil.getObjectSafe(capturedEnv, slot)
                    frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Object)
                    frame.setObject(slot, value)
                }
                else -> Unit
            }
        }
        return bodyNode.executeGeneric(frame)
    }

}