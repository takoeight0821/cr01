package language

import language.nodes.builder.CrNodeFactory
import language.nodes.expr.ExprNode
import language.parser.Cr01BaseListener
import language.parser.Cr01Parser.*
import language.value.CrFunction
import org.antlr.v4.runtime.Token
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

class Cr01ParseTreeListener internal constructor(language: CrLanguage) : Cr01BaseListener() {
    private val functions: MutableMap<String, CrFunction> = HashMap()
    private val nodes: Stack<ExprNode> = Stack()
    private val factory: CrNodeFactory = CrNodeFactory(language)

    fun getFunctions(): Map<String, CrFunction> {
        return functions
    }

    override fun enterFunDecl(ctx: FunDeclContext) {
        val crFunctionBuilder = factory.startToplevelFunction(ctx.name.text)
        ctx.params.forEach(Consumer { param: Token ->
            crFunctionBuilder.addParameter(
                param.text
            )
        })
    }

    override fun exitFunDecl(ctx: FunDeclContext) {
        val function = factory.endFunction(nodes.pop())
        functions[function.name] = function
    }

    override fun exitApplyExpr(ctx: ApplyExprContext) {
        if (ctx.args.size != 0) {
            val args = arrayOfNulls<ExprNode>(ctx.args.size)
            for (i in 1..ctx.args.size) {
                args[ctx.args.size - i] = nodes.pop()
            }
            val func = nodes.pop()
            nodes.push(factory.createApply(func, args.requireNoNulls()))
        }
    }

    override fun exitInfixExpr(ctx: InfixExprContext) {
        val right = nodes.pop()
        val left = nodes.pop()
        val op: Int = ctx.op.type
        nodes.push(factory.createInfix(op, left, right))
    }

    override fun exitNumberExpr(ctx: NumberExprContext) {
        val text = ctx.value.text
        nodes.push(factory.createNumber(text.toLong()))
    }

    override fun exitVarExpr(ctx: VarExprContext) {
        val name = ctx.name.text
        nodes.push(factory.createVar(name))
    }

    override fun exitSimpleDecl(ctx: SimpleDeclContext) {
        val name = ctx.name.text
        val value = nodes.pop()
        factory.currentLet.addSimpleDecl(name, value)
    }

    override fun enterLetExpr(ctx: LetExprContext) {
        factory.startLet()
    }

    override fun exitLetExpr(ctx: LetExprContext) {
        val bodyNode = nodes.pop()
        nodes.push(factory.endLet(bodyNode))
    }

    override fun enterFnExpr(ctx: FnExprContext) {
        val builder = factory.startFunction("lambda#" + UUID.randomUUID())
        ctx.params.forEach(Consumer { param: Token ->
            builder.addParameter(
                param.text
            )
        })
    }

    override fun exitFnExpr(ctx: FnExprContext) {
        nodes.push(factory.createFunctionExpr(nodes.pop()))
    }

}