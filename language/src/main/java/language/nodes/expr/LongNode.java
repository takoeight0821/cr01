package language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "value")
public final class LongNode extends ExprNode {
    private long value;

    public LongNode(long value) {
        this.value = value;
    }

    public static LongNode of(String v) {
        return new LongNode(Long.parseLong(v.trim()));
    }

    public long executeLong(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
