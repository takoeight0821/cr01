package language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import language.runtime.CrFunction;

public final class FunctionExprNode extends ExprNode {
    private final CrFunction function;

    public FunctionExprNode(CrFunction function) {
        this.function = function;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return function;
    }
}
