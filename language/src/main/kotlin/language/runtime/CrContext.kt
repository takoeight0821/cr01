package language.runtime

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.NodeInfo
import java.io.PrintWriter
import java.util.HashMap

class CrContext(env: TruffleLanguage.Env) {
    private val functions: MutableMap<String, CrFunction> = HashMap()
    val output: PrintWriter = PrintWriter(env.out(), true)

    fun lookup(name: String): CrFunction? = functions[name]
    fun register(name: String, function: CrFunction) = functions.put(name, function)

    companion object {
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