package language.nodes.builder

import com.oracle.truffle.api.frame.FrameDescriptor
import language.CrLanguage
import language.nodes.expr.*
import language.parser.Cr01Lexer
import language.runtime.CrFunction
import java.util.*

class CrNodeFactory(private val language: CrLanguage) {
    private var frameDescriptor: FrameDescriptor? = null
    private var lexicalScope: LexicalScope? = null
    private val functionBuilders = LinkedList<CrFunctionBuilder>()
    private val letExprBuilders = LinkedList<LetExprBuilder>()
    /*
    CrFunctionBuilder
     */
    fun startToplevelFunction(): CrFunctionBuilder {
        frameDescriptor = FrameDescriptor()
        return startFunction()
    }

    fun startFunction(): CrFunctionBuilder {
        lexicalScope = language.nodes.builder.LexicalScope(lexicalScope)
        val functionBuilder = CrFunctionBuilder(frameDescriptor!!, lexicalScope!!, language)
        functionBuilders.addFirst(functionBuilder)
        return functionBuilder
    }

    fun endFunction(bodyNode: ExprNode?): CrFunction {
        val functionBuilder = functionBuilders.removeFirst()
        lexicalScope = lexicalScope!!.outer
        return functionBuilder.buildCrFunction(bodyNode)
    }

    fun createFunctionExpr(bodyNode: ExprNode?): FunctionExprNode {
        val functionBuilder = functionBuilders.removeFirst()
        return functionBuilder.buildFunctionExprNode(bodyNode)
    }

    val currentFunction: CrFunctionBuilder
        get() = functionBuilders.peekFirst()

    fun startLet() {
        lexicalScope = language.nodes.builder.LexicalScope(lexicalScope)
        val letExprBuilder = LetExprBuilder(lexicalScope!!, frameDescriptor!!)
        letExprBuilders.addFirst(letExprBuilder)
    }

    fun endLet(bodyNode: ExprNode?): LetNode {
        val letExprBuilder = letExprBuilders.removeFirst()
        lexicalScope = lexicalScope!!.outer
        return letExprBuilder.buildLetNode(bodyNode)
    }

    val currentLet: LetExprBuilder
        get() = letExprBuilders.peekFirst()

    /**
     * create the node representing variable and push it to this.nodes;
     *
     * @param name
     */
    fun createVar(name: String?): ExprNode {
        return if (lexicalScope!!.locals.containsKey(name)) {
            val frameSlot = lexicalScope!!.locals[name]
            VariableNodeGen.create(frameSlot)
        } else {
            FunctionNameNode(language, name)
        }
    }

    fun createApply(funcNode: ExprNode?, argNodes: Array<ExprNode?>?): InvokeNode {
        return language.nodes.expr.InvokeNode(funcNode, argNodes)
    }

    fun createInfix(opType: Int, left: ExprNode?, right: ExprNode?): BinaryNode? {
        assert(opType == Cr01Lexer.OP_ADD || opType == Cr01Lexer.OP_SUB || opType == Cr01Lexer.OP_MUL || opType == Cr01Lexer.OP_DIV)
        when (opType) {
            Cr01Lexer.OP_ADD -> {
                return AddNodeGen.create(left, right)
            }
            Cr01Lexer.OP_SUB -> {
                return SubNodeGen.create(left, right)
            }
            Cr01Lexer.OP_MUL -> {
                return MulNodeGen.create(left, right)
            }
            Cr01Lexer.OP_DIV -> {
                return DivNodeGen.create(left, right)
            }
        }
        return null
    }

    fun createNumber(value: Long): LongNode {
        return LongNode(value)
    }

}