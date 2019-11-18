package language.nodes.builder;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import language.CrLanguage;
import language.nodes.CrRootNode;
import language.nodes.expr.ExprNode;
import language.nodes.expr.LetNode;
import language.nodes.expr.ReadArgumentNode;
import language.nodes.stmt.SimpleDeclNode;
import language.nodes.stmt.SimpleDeclNodeGen;
import language.runtime.CrFunction;

import java.util.LinkedList;

public final class CrFunctionBuilder {
    private String functionName;
    private final FrameDescriptor frameDescriptor;
    private LinkedList<SimpleDeclNode> parameterNodes = new LinkedList<>();
    private LexicalScope lexicalScope;

    private final CrLanguage language;
    private RootCallTarget rootNode;

    CrFunctionBuilder(FrameDescriptor frameDescriptor, LexicalScope lexicalScope, CrLanguage language) {
        this.frameDescriptor = frameDescriptor;
        this.lexicalScope = lexicalScope;
        this.language = language;
    }

    public CrFunctionBuilder setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public CrFunctionBuilder addParameter(String name) {
        var frameSlot = frameDescriptor.addFrameSlot(new Object());
        lexicalScope.locals.put(name, frameSlot);
        parameterNodes.push(SimpleDeclNodeGen.create(new ReadArgumentNode(parameterNodes.size()), frameSlot));
        return this;
    }

    private void addBodyNode(ExprNode bodyNode) {
        bodyNode = new LetNode(parameterNodes.toArray(new SimpleDeclNode[0]), bodyNode);
        rootNode = Truffle.getRuntime().createCallTarget(new CrRootNode(language, frameDescriptor, bodyNode));

    }

    CrFunction buildCrFunction(ExprNode bodyNode) {
        this.addBodyNode(bodyNode);
        return new CrFunction(functionName, parameterNodes.size(), rootNode);
    }
}
