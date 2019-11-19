package language.nodes.builder;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import language.CrLanguage;
import language.nodes.CrRootNode;
import language.nodes.expr.ExprNode;
import language.nodes.expr.FunctionExprNode;
import language.nodes.expr.LetNode;
import language.nodes.expr.ReadArgumentNode;
import language.nodes.stmt.SimpleDeclNode;
import language.nodes.stmt.SimpleDeclNodeGen;
import language.runtime.CrFunction;

import java.util.LinkedList;
import java.util.UUID;

public final class CrFunctionBuilder {
    private String functionName;
    private final FrameDescriptor frameDescriptor;
    private LinkedList<SimpleDeclNode> parameterNodes = new LinkedList<>();
    private LexicalScope lexicalScope;

    private final CrLanguage language;

    CrFunctionBuilder(FrameDescriptor frameDescriptor, LexicalScope lexicalScope, CrLanguage language) {
        this.frameDescriptor = frameDescriptor;
        this.lexicalScope = lexicalScope;
        this.language = language;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void addParameter(String name) {
        var frameSlot = frameDescriptor.addFrameSlot(name + "#" + UUID.randomUUID());
        lexicalScope.locals.put(name, frameSlot);
        parameterNodes.push(SimpleDeclNodeGen.create(new ReadArgumentNode(parameterNodes.size()), frameSlot));
    }

    CrFunction buildCrFunction(ExprNode bodyNode) {
        return new CrFunction(language, frameDescriptor, functionName, parameterNodes.size(),
                Truffle.getRuntime().createCallTarget(new CrRootNode(language, frameDescriptor, new LetNode(parameterNodes.toArray(new SimpleDeclNode[0]), bodyNode))));
    }

    FunctionExprNode buildFunctionExprNode(ExprNode bodyNode) {
        return new FunctionExprNode(this.language, this.parameterNodes, bodyNode);
    }
}
