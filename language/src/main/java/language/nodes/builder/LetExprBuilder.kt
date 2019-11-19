package language.nodes.builder

import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.frame.FrameSlotKind
import language.nodes.expr.ExprNode
import language.nodes.expr.LetNode
import language.nodes.stmt.SimpleDeclNode
import language.nodes.stmt.SimpleDeclNodeGen
import java.util.*

class LetExprBuilder internal constructor(private val lexicalScope: LexicalScope, private val frameDescriptor: FrameDescriptor) {
    private val simpleDeclNodes = LinkedList<SimpleDeclNode>()
    fun addSimpleDecl(name: String, value: ExprNode?) {
        val frameSlot = frameDescriptor.addFrameSlot(name + "#" + UUID.randomUUID(), FrameSlotKind.Illegal)
        lexicalScope.locals[name] = frameSlot
        simpleDeclNodes.push(SimpleDeclNodeGen.create(value, frameSlot))
    }

    fun buildLetNode(bodyNode: ExprNode?): LetNode {
        return LetNode(simpleDeclNodes.toTypedArray(), bodyNode)
    }

}