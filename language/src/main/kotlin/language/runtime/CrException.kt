package language.runtime

import com.oracle.truffle.api.TruffleException
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.nodes.Node
import language.CrLanguage

class CrException private constructor(message: String, private val location: Node) : RuntimeException(message),
    TruffleException {
    override fun getLocation(): Node {
        return location
    }

    companion object {
        fun typeError(operation: Node, vararg values: Any?): CrException {
            val result = StringBuilder()
            result.append("Type error")
            val ss = operation.encapsulatingSourceSection
            if (ss != null && ss.isAvailable) {
                result.append(" at ").append(ss.source.name).append(" line ").append(ss.startLine).append(" col ")
                    .append(ss.startColumn)
            }
            result.append(": operation")
            val nodeInfo = CrContext.lookupNodeInfo(operation.javaClass)
            if (nodeInfo != null) {
                result.append(" \"").append(nodeInfo.shortName).append("\"")
            }
            result.append(" not defined for")
            var sep = " "
            for (value in values) {
                result.append(sep)
                sep = ", "
                if (value == null) {
                    result.append("null")
                } else if (InteropLibrary.getFactory().uncached.isNull(value)) {
                    result.append(CrLanguage.toString(value))
                } else {
                    result.append(CrLanguage.getTypeInfo(value))
                    result.append(" ")
                    if (InteropLibrary.getFactory().uncached.isString(value)) {
                        result.append("\"")
                    }
                    result.append(CrLanguage.toString(value))
                    if (InteropLibrary.getFactory().uncached.isString(value)) {
                        result.append("\"")
                    }
                }
            }
            return CrException(result.toString(), operation)
        }
    }

}