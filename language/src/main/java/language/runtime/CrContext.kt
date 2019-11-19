package language.runtime

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.NodeInfo
import language.CrLanguage

class CrContext(private val language: CrLanguage) {
    val functionRegistry: CrFunctionRegistry = CrFunctionRegistry(language)

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
            if (aClass == null) {
                return null
            }
            val info =
                aClass.getAnnotation(NodeInfo::class.java)
            return info ?: lookupNodeInfo(aClass.superclass)
        }
    }

}