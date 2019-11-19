package language.nodes.builder

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.FrameDescriptor
import language.CrLanguage
import language.nodes.CrRootNode
import language.nodes.expr.ExprNode
import language.nodes.expr.FunctionExprNode
import language.nodes.expr.LetNode
import language.nodes.expr.ReadArgumentNode
import language.nodes.stmt.SimpleDeclNode
import language.nodes.stmt.SimpleDeclNodeGen
import language.runtime.CrFunction
import java.util.*

class CrFunctionBuilder internal constructor(private val frameDescriptor: FrameDescriptor, private val lexicalScope: LexicalScope, private val language: CrLanguage) {
    private var functionName: String? = null
    private val parameterNodes = LinkedList<SimpleDeclNode>()
    fun setFunctionName(functionName: String?) {
        this.functionName = functionName
    }

    fun addParameter(name: String) {
        val frameSlot = frameDescriptor.addFrameSlot(name + "#" + UUID.randomUUID())
        lexicalScope.locals[name] = frameSlot
        parameterNodes.push(SimpleDeclNodeGen.create(ReadArgumentNode(parameterNodes.size), frameSlot))
    }

    fun buildCrFunction(bodyNode: ExprNode?): CrFunction {
        return CrFunction(language, frameDescriptor, functionName, parameterNodes.size,
                Truffle.getRuntime().createCallTarget(CrRootNode(language, frameDescriptor, LetNode(parameterNodes.toTypedArray(), bodyNode))))
    }

    fun buildFunctionExprNode(bodyNode: ExprNode?): FunctionExprNode {
        return FunctionExprNode(language, parameterNodes, bodyNode)
    }

}