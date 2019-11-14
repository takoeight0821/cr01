package language.nodes.expr;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import language.nodes.CrTypes;
import language.nodes.CrTypesGen;
import language.runtime.CrFunction;

@TypeSystemReference(CrTypes.class)
@NodeInfo(description = "The abstract base node for all expressions")
public abstract class ExprNode extends Node {
    public abstract Object executeGeneric(VirtualFrame frame);

    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return CrTypesGen.expectLong(executeGeneric(frame));
    }

    public CrFunction executeCrFunction(VirtualFrame frame) throws UnexpectedResultException {
        return CrTypesGen.expectCrFunction(executeGeneric(frame));
    }
}
