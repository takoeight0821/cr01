package language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import language.runtime.CrNull;
import org.jetbrains.annotations.NotNull;

public class ReadArgumentNode extends ExprNode {
    private final int index;

    private final BranchProfile outOfBoundsTaken = BranchProfile.create();

    public ReadArgumentNode(int index) {
        this.index = index;
    }

    @NotNull
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (index < args.length) {
            return args[index];
        } else {
            outOfBoundsTaken.enter();
            return CrNull.INSTANCE;
        }
    }
}
