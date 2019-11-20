package language.nodes

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.TruffleLanguage.ContextReference
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.nodes.DirectCallNode
import com.oracle.truffle.api.nodes.RootNode
import language.CrLanguage
import language.nodes.builtins.builtins
import language.runtime.CrContext
import language.runtime.CrFunction
import language.runtime.CrNull
import java.util.*

/**
 * This class performs two additional tasks: (as SimpleLanguage https://github.com/graalvm/simplelanguage/)
 * 1. Lazily registeration of functions on first execution. This fulfills the semantics of "evaluating"
 * source code in Cr01.
 * 2. Conversion of arguments to types understood by Cr01. The Cr01 source code can be evaluated from a
 * different language, i.e., the caller can be a node from a different language that uses types not
 * understood by Cr01.
 */
class CrEvalRootNode(
    private val language: CrLanguage,
    rootFunction: RootCallTarget?,
    private val functions: Map<String, CrFunction>
) : RootNode(language) {
    @Child
    private var mainCallNode: DirectCallNode? = if (rootFunction != null) DirectCallNode.create(rootFunction) else null
    private val reference: ContextReference<CrContext> = language.contextReference
    @CompilationFinal
    private var registered = false

    override fun isInstrumentable(): Boolean {
        return false
    }

    override fun getName(): String {
        return "root eval"
    }

    override fun toString(): String {
        return name
    }

    override fun execute(frame: VirtualFrame): Any { /* Lazy registrations of functions on first execution. */
        if (!registered) {
            CompilerDirectives.transferToInterpreterAndInvalidate()
            functions.forEach { (name: String?, crFunction: CrFunction?) ->
                reference.get().functionRegistry.register(name, crFunction)
            }
            builtins(language).forEach { (name, crFunction) ->
                reference.get().functionRegistry.register(name, crFunction)
            }
            registered = true
        }
        return if (mainCallNode == null) { /* The source code did not have a main function, so nothing to execute. */
            CrNull
        } else {
            val arguments = Arrays.stream(frame.arguments).map { obj: Any -> CrContext.fromForeignValue(obj) }.toArray()
            mainCallNode!!.call(*arguments)
        }
    }

}