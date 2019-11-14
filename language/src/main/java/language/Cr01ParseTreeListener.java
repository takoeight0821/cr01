package language;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import language.nodes.CrRootNode;
import language.nodes.expr.*;
import language.nodes.stmt.SimpleDecl;
import language.nodes.stmt.SimpleDeclNodeGen;
import language.parser.Cr01BaseListener;
import language.parser.Cr01Lexer;
import language.parser.Cr01Parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Cr01ParseTreeListener extends Cr01BaseListener {
    private Map<String, RootCallTarget> functions;
    private LinkedList<ExprNode> nodes = new LinkedList<>();
    private FrameDescriptor frameDescriptor;
    private String functionName;
    private LinkedList<SimpleDecl> parameterDecls;
    private CrLanguage language;

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

    private LexicalScope lexicalScope;

    public Cr01ParseTreeListener(CrLanguage language) {
        this.language = language;
        this.functions = new HashMap<>();
    }

    Map<String, RootCallTarget> getFunctions() {
        return functions;
    }

    @Override
    public void enterFunDecl(Cr01Parser.FunDeclContext ctx) {
        functionName = ctx.name.getText();
        frameDescriptor = new FrameDescriptor();
        parameterDecls = new LinkedList<>();
        lexicalScope = new LexicalScope(null);
        for (int i = 0; i < ctx.params.size(); i++) {
            var frameSlot = frameDescriptor.addFrameSlot(ctx.params.get(i).getText());
            parameterDecls.push(SimpleDeclNodeGen.create(new ReadArgumentNode(i),
                    frameSlot));
            lexicalScope.locals.put(ctx.params.get(i).getText(), frameSlot);
        }
    }

    @Override
    public void exitFunDecl(Cr01Parser.FunDeclContext ctx) {
        functions.put(functionName, Truffle.getRuntime().createCallTarget(new CrRootNode(language, frameDescriptor, nodes.pop())));
    }

    @Override
    public void exitInfixExpr(Cr01Parser.InfixExprContext ctx) {
        var right = nodes.pop();
        var left = nodes.pop();

        ExprNode current = null;
        switch (ctx.op.getType()) {
            case Cr01Lexer.OP_ADD:
                current = AddNodeGen.create(left, right);
                break;
            case Cr01Lexer.OP_SUB:
                current = SubNodeGen.create(left, right);
                break;
            case Cr01Lexer.OP_MUL:
                current = MulNodeGen.create(left, right);
                break;
            case Cr01Lexer.OP_DIV:
                current = DivNodeGen.create(left, right);
                break;
        }
        nodes.push(current);
    }

    @Override
    public void exitNumberExpr(Cr01Parser.NumberExprContext ctx) {
        var text = ctx.value.getText();
        nodes.push(new LongNode(Long.parseLong(text)));
    }

    @Override
    public void exitVarExpr(Cr01Parser.VarExprContext ctx) {
        String name = ctx.name.getText();
        FrameSlot frameSlot = lexicalScope.locals.get(name);
        nodes.push(VariableNodeGen.create(frameSlot));
    }

    private SimpleDecl declNode;

    @Override
    public void exitSimpleDecl(Cr01Parser.SimpleDeclContext ctx) {
        String name = ctx.name.getText();
        ExprNode value = nodes.pop();
        FrameSlot frameSlot = frameDescriptor.findOrAddFrameSlot(name, FrameSlotKind.Illegal);
        lexicalScope.locals.put(name, frameSlot);
        declNode = SimpleDeclNodeGen.create(value, frameSlot);
    }

    @Override
    public void enterLetExpr(Cr01Parser.LetExprContext ctx) {
        lexicalScope = new LexicalScope(lexicalScope);
    }

    @Override
    public void exitLetExpr(Cr01Parser.LetExprContext ctx) {
        ExprNode bodyNode = nodes.pop();
        SimpleDecl[] declNodes = { declNode };
        nodes.push(new LetNode(declNodes, bodyNode));
        lexicalScope = lexicalScope.outer;
    }
}