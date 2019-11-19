package language.nodes.expr

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.VirtualFrame
import language.CrLanguage
import language.nodes.CrRootNode
import language.nodes.stmt.SimpleDeclNode
import language.runtime.CrFunction
import java.util.*

class FunctionExprNode(
    private val language: CrLanguage,
    private val parameterList: List<SimpleDeclNode>,
    private val bodyNode: ExprNode
) : ExprNode() {
    override fun executeCrFunction(frame: VirtualFrame): CrFunction {
        val capturedEnv = frame.materialize()
        val rootCallTarget = Truffle.getRuntime().createCallTarget(
            CrRootNode(
                language,
                frame.frameDescriptor,
                LetNode(parameterList.toTypedArray(), bodyNode),
                capturedEnv
            )
        )
        return CrFunction(
            language,
            frame.frameDescriptor,
            "lambda#" + UUID.randomUUID(),
            parameterList.size,
            rootCallTarget
        )
    }

    override fun executeGeneric(frame: VirtualFrame): Any = executeCrFunction(frame)

}