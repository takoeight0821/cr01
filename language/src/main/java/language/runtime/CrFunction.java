package language.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;
import language.CrLanguage;

@ExportLibrary(InteropLibrary.class)
public final class CrFunction implements TruffleObject {
    public static final int INLINE_CACHE_SIZE = 2;

    /**
     * The name of the function.
     */
    private final String name;

    /**
     * The implementation of this function.
     */
    private final RootCallTarget callTarget;

    public CrFunction(String name, RootCallTarget rootCallTarget) {
        this.name = name;
        this.callTarget = rootCallTarget;
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    @Override
    public String toString() {
        return name;
    }

    @SuppressWarnings("static-method")
    public SourceSection getDeclaredLocation() {
        return getCallTarget().getRootNode().getSourceSection();
    }

    @SuppressWarnings("static-method")
    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    abstract static class Execute {
        @Specialization(limit = "INLINE_CACHE_SIZE",
        guards = "function.getCallTarget() == cachedTarget")
        @SuppressWarnings("unused")
        protected static Object doDirect(CrFunction function, Object[] arguments,
                                         @Cached("function.getCallTarget()") RootCallTarget cachedTarget,
                                         @Cached("create(cachedTarget)")DirectCallNode callNode) {
            return callNode.call(arguments);
        }

        @Specialization(replaces = "doDirect")
        protected static Object doIndirect(CrFunction function, Object[] arguments,
                                           @Cached IndirectCallNode callNode) {
            return callNode.call(function.getCallTarget(), arguments);
        }
    }
}
