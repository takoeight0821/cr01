package language.nodes.builtins

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.dsl.CachedContext
import com.oracle.truffle.api.dsl.NodeChild
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.nodes.NodeInfo
import language.CrLanguage
import language.nodes.CrRootNode
import language.nodes.expr.ExprNode
import language.nodes.expr.ReadArgumentNode
import language.runtime.CrContext
import language.value.CrFunction
import java.io.PrintWriter

@NodeChild(value = "arguments", type = Array<ExprNode>::class)
abstract class BuiltinNode : ExprNode() {
    companion object {
        fun builtin(
            language: CrLanguage,
            name: String,
            arity: Int,
            builtinNode: BuiltinNode
        ): Pair<String, CrFunction> {
            val frameDescriptor = FrameDescriptor()
            return name to CrFunction(
                language, frameDescriptor, name, arity, Truffle.getRuntime().createCallTarget(
                    CrRootNode(language, frameDescriptor, builtinNode)
                )
            )
        }
    }
}

@NodeInfo(shortName = "println")
abstract class PrintlnBuiltin : BuiltinNode() {
    @Specialization
    fun println(value: Long, @CachedContext(CrLanguage::class) context: CrContext): Long {
        doPrint(context.output, value)
        return value
    }

    @CompilerDirectives.TruffleBoundary
    private fun doPrint(output: PrintWriter, value: Long) {
        output.println(value)
    }

    @Specialization
    fun println(value: Any, @CachedContext(CrLanguage::class) context: CrContext): Any {
        doPrint(context.output, value)
        return value
    }

    @CompilerDirectives.TruffleBoundary
    private fun doPrint(output: PrintWriter, value: Any) {
        output.println(value)
    }

    companion object {
        fun printlnBuiltin(language: CrLanguage): Pair<String, CrFunction> =
            builtin(language, "println", 1, PrintlnBuiltinNodeGen.create(arrayOf(ReadArgumentNode(1))))
    }
}

fun builtins(language: CrLanguage): Map<String, CrFunction> = mapOf(PrintlnBuiltin.printlnBuiltin(language))