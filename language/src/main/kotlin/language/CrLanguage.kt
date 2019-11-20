package language

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.source.Source
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
        val evalMain = functions["main"]?.callTarget?.let { CrEvalRootNode(this, it, functions) }
            ?: error("function 'main' is not defined")
        return Truffle.getRuntime().createCallTarget(evalMain)
    }

    private fun parseSource(source: Source): Map<String, CrFunction> {
        val parser = Cr01Parser(CommonTokenStream(Cr01Lexer(CharStreams.fromString(source.characters.toString()))))
        val listener = Cr01ParseTreeListener(this)
        ParseTreeWalker().walk(listener, parser.prog())
        return listener.getFunctions()
    }

    override fun createContext(env: Env): CrContext = CrContext(env)

    override fun isObjectOfLanguage(`object`: Any): Boolean = false

    companion object {
        const val MIME = "application/x-cr01"
        fun toString(value: Any): String = value.toString()

        fun getTypeInfo(value: Any?): String {
            if (value == null) {
                return "ANY"
            }
            val interop = InteropLibrary.getFactory().getUncached(value)
            return when {
                interop.isNumber(value) -> "Number"
                interop.isBoolean(value) -> "Boolean"
                interop.isString(value) -> "String"
                interop.isNull(value) -> "NULL"
                interop.isExecutable(value) -> "Function"
                interop.hasMembers(value) -> "Object"
                else -> "Unsupported"
            }
        }
    }
}