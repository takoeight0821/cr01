package language;

import language.CrLanguage;
import language.nodes.AddNodeGen;
import language.nodes.CrExprNode;
import language.nodes.LongNode;
import language.parser.Cr01BaseListener;
import language.parser.Cr01Lexer;
import language.parser.Cr01Parser;

import java.util.LinkedList;

public class Cr01ParseTreeListener extends Cr01BaseListener {
    private CrExprNode expr;
    private LinkedList<CrExprNode> nodes = new LinkedList<>();

    public CrExprNode getExpr() {
        return expr;
    }

    @Override
    public void exitProg(Cr01Parser.ProgContext ctx) {
        expr = nodes.pop();
    }

    @Override
    public void exitInfixExpr(Cr01Parser.InfixExprContext ctx) {
        var right = nodes.pop();
        var left = nodes.pop();

        CrExprNode current = null;
        switch (ctx.op.getType()) {
            case Cr01Lexer
                    .OP_ADD:
                current = AddNodeGen.create(left, right);
            break;
        }
        nodes.push(current);
    }

    @Override
    public void exitNumberExpr(Cr01Parser.NumberExprContext ctx) {
        var text = ctx.value.getText();
        nodes.push(new LongNode(Long.parseLong(text)));
    }
}
