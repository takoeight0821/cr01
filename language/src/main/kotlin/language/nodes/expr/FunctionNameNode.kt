package language.nodes.expr

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal
import com.oracle.truffle.api.TruffleLanguage.ContextReference
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.NodeInfo
import language.CrLanguage
import language.runtime.CrContext
import language.runtime.CrFunction

@NodeInfo(shortName = "func")
class FunctionNameNode(language: CrLanguage, private val functionName: String) : ExprNode() {
    @CompilationFinal
    private var cachedFunction: CrFunction? = null
    private val reference: ContextReference<CrContext> = language.contextReference
    override fun executeGeneric(frame: VirtualFrame): Any = executeCrFunction(frame)

    override fun executeCrFunction(frame: VirtualFrame): CrFunction = when (cachedFunction) {
        null -> {
            CompilerDirectives.transferToInterpreterAndInvalidate()
            cachedFunction = reference.get().functionRegistry.lookup(functionName)
            cachedFunction!!
        }
        else -> {
            cachedFunction!!
        }
    }

}