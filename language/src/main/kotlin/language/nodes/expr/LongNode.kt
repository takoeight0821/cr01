package language.nodes.expr

import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.NodeInfo

@NodeInfo(shortName = "value")
class LongNode(private val value: Long) : ExprNode() {
    override fun executeLong(frame: VirtualFrame): Long = value

    override fun executeGeneric(frame: VirtualFrame): Any = value
}