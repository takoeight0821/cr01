package language.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrException;

@NodeInfo(shortName = "+")
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class AddNode extends CrExprNode {
    @Specialization
    public long add(long left, long right) {
        return left + right;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw CrException.typeError(this, left, right);
    }
}
