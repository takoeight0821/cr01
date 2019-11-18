package language;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import language.nodes.CrRootNode;
import language.nodes.builder.CrFunctionBuilder;
import language.nodes.builder.CrNodeFactory;
import language.nodes.expr.*;
import language.nodes.stmt.SimpleDeclNode;
import language.nodes.stmt.SimpleDeclNodeGen;
import language.parser.Cr01BaseListener;
import language.parser.Cr01Lexer;
import language.parser.Cr01Parser;
import language.runtime.CrFunction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Cr01ParseTreeListener extends Cr01BaseListener {
    private Map<String, CrFunction> functions;

    private LinkedList<ExprNode> nodes = new LinkedList<>();

    private CrLanguage language;

    private CrNodeFactory factory;

    public Cr01ParseTreeListener(CrLanguage language) {
        this.language = language;
        this.functions = new HashMap<>();
        this.factory = new CrNodeFactory(this.language);
    }

    Map<String, CrFunction> getFunctions() {
        return functions;
    }

    @Override
    public void enterFunDecl(Cr01Parser.FunDeclContext ctx) {
        CrFunctionBuilder crFunctionBuilder = factory.startToplevelFunction();

        crFunctionBuilder.setFunctionName(ctx.name.getText());

        ctx.params.stream().forEach((param) -> crFunctionBuilder.addParameter(param.getText()));
    }

    @Override
    public void exitFunDecl(Cr01Parser.FunDeclContext ctx) {
        CrFunction function = factory.endFunction(nodes.pop());
        functions.put(function.getName(), function);
    }

    @Override
    public void exitApplyExpr(Cr01Parser.ApplyExprContext ctx) {
        if (ctx.args.size() != 0) {
            ExprNode[] args = new ExprNode[ctx.args.size()];
            for (int i = 1; i <= ctx.args.size(); i++) {
                args[ctx.args.size() - i] = nodes.pop();
            }
            ExprNode func = nodes.pop();
            nodes.push(factory.createApply(func, args));
        }
    }

    @Override
    public void exitInfixExpr(Cr01Parser.InfixExprContext ctx) {
        var right = nodes.pop();
        var left = nodes.pop();

        nodes.push(factory.createInfix(ctx.op.getType(), left, right));
    }

    @Override
    public void exitNumberExpr(Cr01Parser.NumberExprContext ctx) {
        var text = ctx.value.getText();
        nodes.push(factory.createNumber(Long.parseLong(text)));
    }

    @Override
    public void exitVarExpr(Cr01Parser.VarExprContext ctx) {
        String name = ctx.name.getText();
        nodes.push(factory.createVar(name));
    }

    private SimpleDeclNode declNode;

    @Override
    public void exitSimpleDecl(Cr01Parser.SimpleDeclContext ctx) {
        String name = ctx.name.getText();
        ExprNode value = nodes.pop();
        factory.getCurrentLet().addSimpleDecl(name, value);
    }

    @Override
    public void enterLetExpr(Cr01Parser.LetExprContext ctx) {
        factory.startLet();
    }

    @Override
    public void exitLetExpr(Cr01Parser.LetExprContext ctx) {
        ExprNode bodyNode = nodes.pop();
        nodes.push(factory.endLet(bodyNode));
    }
}