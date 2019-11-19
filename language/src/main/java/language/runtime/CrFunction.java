package language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.SourceSection;
import language.CrLanguage;
import language.nodes.CrRootNode;
import language.nodes.expr.ExprNode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
クロージャにするための改造方法メモ
MaterializedFrameをCrFunctionのフィールドに追加する。これはキャプチャした環境を表す
CrFunctionの持つルートノードをexecuteするとき、frameにMaterializedFrameの中身をコピーする
FunctionExprNodeのexecuteでMaterializedFrameにframeの中身をコピーする
これでうまくいく？
コピーのためにFrameDescriptorを持っておく必要がある
 */
@ExportLibrary(InteropLibrary.class)
public final class CrFunction implements TruffleObject, Cloneable {
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
    private final CrLanguage language;
    private final FrameDescriptor frameDescriptor;
    private final RootCallTarget callTarget;

    private final int parameterCount;

    private final DirectCallNode callNode;

    private List<Object> appliedArguments = new LinkedList<>();

    public CrFunction(CrLanguage language, FrameDescriptor frameDescriptor, String name, int parameterCount, ExprNode bodyNode) {
        this(language, frameDescriptor, name, parameterCount, Truffle.getRuntime().createCallTarget(new CrRootNode(language, frameDescriptor, bodyNode)));
    }

    private CrFunction(CrLanguage language, FrameDescriptor frameDescriptor, String name, int parameterCount, RootCallTarget callTarget) {
        this.language = language;
        this.frameDescriptor = frameDescriptor;
        this.name = name;
        this.parameterCount = parameterCount;
        this.callTarget = callTarget;
        this.callNode = DirectCallNode.create(this.callTarget);
    }

    private int arity() {
        return parameterCount - appliedArguments.size();
    }

    private CrFunction partialApply(List<Object> args) {
        CrFunction newFunc = new CrFunction(language, frameDescriptor, this.name, this.parameterCount, this.callTarget);
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
