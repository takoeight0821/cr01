package language.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "value")
public class LongNode extends CrExprNode {
    private long value;

    public LongNode(long value) {
        this.value = value;
    }

    public static LongNode of(String v) {
        return new LongNode(Long.parseLong(v.trim()));
    }

    long executeLong(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
