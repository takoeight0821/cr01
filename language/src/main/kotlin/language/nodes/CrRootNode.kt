package language.nodes

import com.oracle.truffle.api.frame.*
import com.oracle.truffle.api.nodes.RootNode
import language.CrLanguage
import language.nodes.expr.ExprNode

class CrRootNode constructor(
    language: CrLanguage,
    frameDescriptor: FrameDescriptor,
    @field:Child private var bodyNode: ExprNode,
    private val capturedEnv: MaterializedFrame? = null
) : RootNode(language, frameDescriptor) {
    override fun execute(frame: VirtualFrame): Any {
        capture(capturedEnv, frame)
        return bodyNode.executeGeneric(frame)
    }

    companion object {
        private fun capture(
            capturedEnv: MaterializedFrame?,
            frame: VirtualFrame
        ) {
            capturedEnv?.frameDescriptor?.slots?.forEach { slot: FrameSlot ->
                when (capturedEnv.frameDescriptor.getFrameSlotKind(slot)!!) {
                    FrameSlotKind.Int -> {
                        val value = FrameUtil.getIntSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Int)
                        frame.setInt(slot, value)
                    }
                    FrameSlotKind.Long -> {
                        val value = FrameUtil.getLongSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Long)
                        frame.setLong(slot, value)
                    }
                    FrameSlotKind.Boolean -> {
                        val value = FrameUtil.getBooleanSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Boolean)
                        frame.setBoolean(slot, value)
                    }
                    FrameSlotKind.Byte -> {
                        val value = FrameUtil.getByteSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Byte)
                        frame.setByte(slot, value)
                    }
                    FrameSlotKind.Double -> {
                        val value = FrameUtil.getDoubleSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Double)
                        frame.setDouble(slot, value)
                    }
                    FrameSlotKind.Float -> {
                        val value = FrameUtil.getFloatSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Float)
                        frame.setFloat(slot, value)
                    }
                    FrameSlotKind.Object -> {
                        val value = FrameUtil.getObjectSafe(capturedEnv, slot)
                        frame.frameDescriptor.setFrameSlotKind(slot, FrameSlotKind.Object)
                        frame.setObject(slot, value)
                    }
                    FrameSlotKind.Illegal -> Unit
                }
            }
        }
    }
}