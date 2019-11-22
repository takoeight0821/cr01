package language.nodes.builtins

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
        context.output.println(value)
        return value
    }

    @Specialization
    fun println(value: Boolean, @CachedContext(CrLanguage::class) context: CrContext): Boolean {
        context.output.println(value)
        return value
    }

    @Specialization
    fun println(value: Any, @CachedContext(CrLanguage::class) context: CrContext): Any {
        context.output.println(value)
        return value
    }

    companion object {
        fun printlnBuiltin(language: CrLanguage): Pair<String, CrFunction> =
            builtin(language, "println", 1, PrintlnBuiltinNodeGen.create(arrayOf(ReadArgumentNode(0))))
    }
}

fun builtins(language: CrLanguage): Map<String, CrFunction> = mapOf(PrintlnBuiltin.printlnBuiltin(language))