package language.nodes.builder

import com.oracle.truffle.api.frame.FrameSlot
import java.util.*

internal class LexicalScope(val outer: LexicalScope?) {
    @JvmField
    val locals: MutableMap<String, FrameSlot>

    init {
        locals = HashMap()
        if (outer != null) {
            locals.putAll(outer.locals)
        }
    }
}