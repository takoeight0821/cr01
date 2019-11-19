package language.nodes.expr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrLanguage;
import language.runtime.CrContext;
import language.runtime.CrFunction;
import org.jetbrains.annotations.NotNull;

@NodeInfo(shortName = "func")
public final class FunctionNameNode extends ExprNode {
    private final String functionName;
    @CompilationFinal
    private CrFunction cachedFunction;
    private final ContextReference<CrContext> reference;

    public FunctionNameNode(CrLanguage language, String functionName) {
        this.functionName = functionName;
        this.reference = language.getContextReference();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeCrFunction(frame);
    }

    @NotNull
    @Override
    public CrFunction executeCrFunction(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = reference.get().getFunctionRegistry().lookup(functionName);
        }
        assert cachedFunction != null;
        return cachedFunction;
    }
}
