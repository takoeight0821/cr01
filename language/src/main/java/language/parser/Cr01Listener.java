// Generated from Cr01.g4 by ANTLR 4.7.2
package language.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Cr01Parser}.
 */
public interface Cr01Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Cr01Parser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(Cr01Parser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link Cr01Parser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(Cr01Parser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code infixExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInfixExpr(Cr01Parser.InfixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code infixExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInfixExpr(Cr01Parser.InfixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumberExpr(Cr01Parser.NumberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumberExpr(Cr01Parser.NumberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParensExpr(Cr01Parser.ParensExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link Cr01Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParensExpr(Cr01Parser.ParensExprContext ctx);
}