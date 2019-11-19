package language

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.source.Source
import language.CrLanguage
import language.nodes.CrEvalRootNode
import language.parser.Cr01Lexer
import language.parser.Cr01Parser
import language.runtime.CrContext
import language.runtime.CrFunction
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

@TruffleLanguage.Registration(
    name = "cr01",
    id = "cr01",
    defaultMimeType = CrLanguage.MIME,
    characterMimeTypes = [CrLanguage.MIME]
)
class CrLanguage : TruffleLanguage<CrContext>() {
    override fun parse(request: ParsingRequest): CallTarget {
        val functions = parseSource(request.source)
        val main = functions["main"]
        val evalMain: RootNode
        evalMain = if (main != null) {
            CrEvalRootNode(this, main.callTarget, functions)
        } else {
            CrEvalRootNode(this, null, functions)
        }
        return Truffle.getRuntime().createCallTarget(evalMain)
    }

    private fun parseSource(source: Source): Map<String, CrFunction> {
        val charStream =
            CharStreams.fromString(source.characters.toString())
        val lexer = Cr01Lexer(charStream)
        val parser = Cr01Parser(CommonTokenStream(lexer))
        val prog = parser.prog()
        val treeWalker = ParseTreeWalker()
        val listener = Cr01ParseTreeListener(this)
        treeWalker.walk(listener, prog)
        return listener.getFunctions()
    }

    override fun createContext(env: Env): CrContext {
        return CrContext(this)
    }

    override fun isObjectOfLanguage(`object`: Any): Boolean {
        return false
    }

    companion object {
        const val MIME = "application/x-cr01"
        fun toString(value: Any): String {
            return value.toString()
        }

        fun getTypeInfo(value: Any?): String {
            if (value == null) {
                return "ANY"
            }
            val interop = InteropLibrary.getFactory().getUncached(value)
            return if (interop.isNumber(value)) {
                "Number"
            } else if (interop.isBoolean(value)) {
                "Boolean"
            } else if (interop.isString(value)) {
                "String"
            } else if (interop.isNull(value)) {
                "NULL"
            } else if (interop.isExecutable(value)) {
                "Function"
            } else if (interop.hasMembers(value)) {
                "Object"
            } else {
                "Unsupported"
            }
        }
    }
}