package language.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrException;

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
