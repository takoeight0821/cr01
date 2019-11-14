package language.nodes.expr;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrException;

@NodeInfo(shortName = "+")
public abstract class AddNode extends BinaryNode {
    @Specialization
    long add(long left, long right) {
        return left + right;
    }

    @Fallback
    Object typeError(Object left, Object right) {
        throw CrException.typeError(this, left, right);
    }
}
