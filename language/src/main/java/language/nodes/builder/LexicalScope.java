package language.nodes.builder;

import com.oracle.truffle.api.frame.FrameSlot;

import java.util.HashMap;
import java.util.Map;

class LexicalScope {
    final LexicalScope outer;
    final Map<String, FrameSlot> locals;

    LexicalScope(LexicalScope outer) {
        this.outer = outer;
        this.locals = new HashMap<>();
        if (outer != null) {
            locals.putAll(outer.locals);
        }
    }
}
