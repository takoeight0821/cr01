package language.nodes.expr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrLanguage;
import language.runtime.CrContext;
import language.runtime.CrFunction;

@NodeInfo(shortName = "func")
public final class FunctionNameNode extends ExprNode {
    private final String functionName;
    @CompilationFinal private CrFunction cachedFunction;
    private final ContextReference<CrContext> reference;
    public FunctionNameNode(CrLanguage language, String functionName) {
        this.functionName = functionName;
        this.reference = language.getContextReference();
    }

    @Override
    public CrFunction executeGeneric(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = reference.get().getFunctionRegistry().lookup(functionName);
        }
        return cachedFunction;
    }
}
