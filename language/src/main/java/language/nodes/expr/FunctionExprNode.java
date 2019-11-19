package language.nodes.expr;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import language.CrLanguage;
import language.nodes.CrRootNode;
import language.nodes.stmt.SimpleDeclNode;
import language.runtime.CrFunction;

import java.util.List;
import java.util.UUID;

public final class FunctionExprNode extends ExprNode {
    private final CrLanguage language;
    private final List<SimpleDeclNode> parameterList;
    private final ExprNode bodyNode;

    public FunctionExprNode(CrLanguage language, List<SimpleDeclNode> parameterList, ExprNode bodyNode) {
        this.language = language;
        this.parameterList = parameterList;
        this.bodyNode = bodyNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        MaterializedFrame capturedEnv = frame.materialize();
        RootCallTarget rootCallTarget = Truffle.getRuntime().createCallTarget(new CrRootNode(language, frame.getFrameDescriptor(), new LetNode(parameterList.toArray(new SimpleDeclNode[0]), bodyNode), capturedEnv));
        return new CrFunction(language, frame.getFrameDescriptor(), "lambda#" + UUID.randomUUID(), parameterList.size(), rootCallTarget);
    }
}
