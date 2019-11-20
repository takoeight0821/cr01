package language.runtime

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.NodeInfo
import java.io.PrintWriter

class CrContext(env: TruffleLanguage.Env) {
    val functionRegistry: CrFunctionRegistry = CrFunctionRegistry()
    val output: PrintWriter = PrintWriter(env.out(), true)

    companion object {
        @JvmStatic
        fun fromForeignValue(a: Any): Any {
            return when (a) {
                is Long -> a
                is Number -> fromForeignNumber(a)
                is TruffleObject -> a
                is CrContext -> a
                else -> {
                    CompilerDirectives.transferToInterpreter()
                    throw IllegalStateException(a.toString() + "is not a Truffle value")
                }
            }
        }

        @TruffleBoundary
        private fun fromForeignNumber(a: Any): Long {
            return (a as Number).toLong()
        }

        fun lookupNodeInfo(aClass: Class<*>?): NodeInfo? {
            return if (aClass == null) {
                null
            } else {
                aClass.getAnnotation(NodeInfo::class.java) ?: lookupNodeInfo(aClass.superclass)
            }
        }
    }

}