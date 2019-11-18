package language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExportLibrary(InteropLibrary.class)
public final class CrFunction implements TruffleObject, Cloneable {
    static final int INLINE_CACHE_SIZE = 2;

    /**
     * The name of the function.
     */
    private final String name;

    public String getName() {
        return this.name;
    }

    /**
     * The implementation of this function.
     */
    private final RootCallTarget callTarget;

    private final int parameterCount;

    private final DirectCallNode callNode;

    private List<Object> appliedArguments = new LinkedList<>();

    public CrFunction(String name, int parameterCount, RootCallTarget rootCallTarget) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.callTarget = rootCallTarget;
        this.callNode = DirectCallNode.create(this.callTarget);
    }

    public List<Object> getAppliedArguments() {
        return this.appliedArguments;
    }

    public int arity() {
        return parameterCount - appliedArguments.size();
    }

    private CrFunction partialApply(List<Object> args) {
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
    Object execute(Object[] arguments) throws ArityException {
        if (this.arity() < arguments.length) {
            CompilerDirectives.transferToInterpreter();
            throw ArityException.create(this.arity(), arguments.length);
        } else if (this.arity() > arguments.length) {
            return this.partialApply(Arrays.stream(arguments).collect(Collectors.toList()));
        } else {
            Object[] actual_arguments = Stream.concat(this.appliedArguments.stream(), Arrays.stream(arguments)).toArray();
            return this.callNode.call(actual_arguments);
        }
    }
}
