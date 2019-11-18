package language.nodes.builder;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import language.CrLanguage;
import language.nodes.expr.*;
import language.nodes.stmt.SimpleDeclNode;
import language.nodes.stmt.SimpleDeclNodeGen;
import language.parser.Cr01Lexer;
import language.runtime.CrFunction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class CrNodeFactory {
    private CrLanguage language;
    private FrameDescriptor frameDescriptor;
    private LexicalScope lexicalScope;

    private LinkedList<CrFunctionBuilder> functionBuilders = new LinkedList<>();
    private LinkedList<LetExprBuilder> letExprBuilders = new LinkedList<>();

    public CrNodeFactory(CrLanguage language) {
        this.language = language;
    }


    /*
    CrFunctionBuilder
     */
    public CrFunctionBuilder startToplevelFunction() {
        frameDescriptor = new FrameDescriptor();
        return startFunction();
    }

    public CrFunctionBuilder startFunction() {
        lexicalScope = new LexicalScope(lexicalScope);
        CrFunctionBuilder functionBuilder = new CrFunctionBuilder(frameDescriptor, lexicalScope, language);
        functionBuilders.addFirst(functionBuilder);
        return functionBuilder;
    }

    public CrFunction endFunction(ExprNode bodyNode) {
        CrFunctionBuilder functionBuilder = functionBuilders.removeFirst();
        return functionBuilder.buildCrFunction(bodyNode);
    }

    public CrFunctionBuilder getCurrentFunction() {
        return functionBuilders.peekFirst();
    }

    public LetExprBuilder startLet() {
        LetExprBuilder letExprBuilder = new LetExprBuilder(lexicalScope, frameDescriptor);
        letExprBuilders.addFirst(letExprBuilder);
        return letExprBuilder;
    }

    public LetNode endLet(ExprNode bodyNode) {
        LetExprBuilder letExprBuilder = letExprBuilders.removeFirst();
        lexicalScope = lexicalScope.outer;
        return letExprBuilder.buildLetNode(bodyNode);
    }

    public LetExprBuilder getCurrentLet() {
        return letExprBuilders.peekFirst();
    }

    /**
     * create the node representing variable and push it to this.nodes;
     *
     * @param name
     */
    public ExprNode createVar(String name) {
        if (lexicalScope.locals.containsKey(name)) {
            FrameSlot frameSlot = lexicalScope.locals.get(name);
            return VariableNodeGen.create(frameSlot);
        } else {
            return new FunctionNameNode(language, name);
        }
    }

    public InvokeNode createApply(ExprNode funcNode, ExprNode[] argNodes) {
        return new InvokeNode(funcNode, argNodes);
    }

    public BinaryNode createInfix(int opType, ExprNode left, ExprNode right) {
        assert (opType == Cr01Lexer.OP_ADD || opType == Cr01Lexer.OP_SUB || opType == Cr01Lexer.OP_MUL || opType == Cr01Lexer.OP_DIV);
        switch (opType) {
            case Cr01Lexer.OP_ADD: {
                return AddNodeGen.create(left, right);
            }
            case Cr01Lexer.OP_SUB: {
                return SubNodeGen.create(left, right);
            }
            case Cr01Lexer.OP_MUL: {
                return MulNodeGen.create(left, right);
            }
            case Cr01Lexer.OP_DIV: {
                return DivNodeGen.create(left, right);
            }
        }
        return null;
    }

    public LongNode createNumber(long value) {
        return new LongNode(value);
    }


    public SimpleDeclNode createSimpleDecl(String name, ExprNode value) {
        FrameSlot frameSlot = frameDescriptor.addFrameSlot(name, FrameSlotKind.Illegal);
        lexicalScope.locals.put(name, frameSlot);
        return SimpleDeclNodeGen.create(value, frameSlot);
    }

    static class LexicalScope {
        final LexicalScope outer;
        final Map<String, FrameSlot> locals;

        LexicalScope(LexicalScope outer) {
            this.outer = outer;
            this.locals = new HashMap<>();
            if (outer != null) {
                locals.putAll(outer.locals);
            }
        }
    }
}
