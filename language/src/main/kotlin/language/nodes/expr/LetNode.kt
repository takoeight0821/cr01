package language.nodes.expr

import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.ExplodeLoop
import com.oracle.truffle.api.nodes.NodeInfo
import language.nodes.stmt.SimpleDeclNode

@NodeInfo(shortName = "let")
class LetNode(
    @field:Children private val declNodes: Array<SimpleDeclNode>,
    @field:Child private var bodyNode: ExprNode
) :
    ExprNode() {

    @ExplodeLoop
    override fun executeGeneric(frame: VirtualFrame): Any {
        for (declNode in declNodes) {
            declNode.executeVoid(frame)
        }
        return bodyNode.executeGeneric(frame)
    }

}