package language.nodes.expr;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrException;
import language.nodes.expr.BinaryNode;

@NodeInfo(shortName = "-")
public abstract class SubNode extends BinaryNode {
    @Specialization
    public long sub(long left, long right) {
        return left - right;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw CrException.typeError(this, left, right);
    }
}
