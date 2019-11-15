package language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

import java.util.LinkedList;
import java.util.List;

@ExportLibrary(InteropLibrary.class)
public final class CrFunction implements TruffleObject, Cloneable {
    static final int INLINE_CACHE_SIZE = 2;

    /**
     * The name of the function.
     */
    private final String name;

    /**
     * The implementation of this function.
     */
    private final RootCallTarget callTarget;

    private final int parameterCount;

    private List<Object> appliedArguments = new LinkedList<>();

    public CrFunction(String name, int parameterCount, RootCallTarget rootCallTarget) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.callTarget = rootCallTarget;
    }

    public List<Object> getAppliedArguments() {
        return this.appliedArguments;
    }

    public int arity() {
        return parameterCount - appliedArguments.size();
    }

    public CrFunction partialApply(List<Object> args) {
        CrFunction newFunc = new CrFunction(this.name, this.parameterCount, this.callTarget);
        newFunc.appliedArguments.addAll(args);
        return newFunc;
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    @Override
    public String toString() {
        return name + appliedArguments + ":" + arity();
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
                                         @Cached("create(cachedTarget)") DirectCallNode callNode) throws ArityException {
            if (function.parameterCount != arguments.length) {
                CompilerDirectives.transferToInterpreter();
                throw ArityException.create(function.arity(), arguments.length - function.appliedArguments.size());
            }
            return callNode.call(arguments);
        }

        @Specialization(replaces = "doDirect")
        protected static Object doIndirect(CrFunction function, Object[] arguments,
                                           @Cached IndirectCallNode callNode) throws ArityException {
            if (function.parameterCount != arguments.length) {
                CompilerDirectives.transferToInterpreter();
                throw ArityException.create(function.arity(), arguments.length - function.appliedArguments.size());
            }
            return callNode.call(function.getCallTarget(), arguments);
        }
    }
}
