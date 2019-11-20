package language.nodes.builder

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.frame.FrameSlotKind
import language.CrLanguage
import language.nodes.CrRootNode
import language.nodes.expr.*
import language.nodes.stmt.SimpleDeclNode
import language.nodes.stmt.SimpleDeclNodeGen
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
    fun startToplevelFunction(functionName: String): CrFunctionBuilder {
        frameDescriptor = FrameDescriptor()
        return startFunction(functionName)
    }

    fun startFunction(functionName: String): CrFunctionBuilder {
        lexicalScope = LexicalScope(lexicalScope)
        val functionBuilder = CrFunctionBuilder(frameDescriptor!!, lexicalScope!!, language, functionName)
        functionBuilders.addFirst(functionBuilder)
        return functionBuilder
    }

    fun endFunction(bodyNode: ExprNode): CrFunction {
        val functionBuilder = functionBuilders.removeFirst()
        lexicalScope = lexicalScope!!.outer
        return functionBuilder.buildCrFunction(bodyNode)
    }

    fun createFunctionExpr(bodyNode: ExprNode): FunctionExprNode =
        functionBuilders.removeFirst().buildFunctionExprNode(bodyNode)

    fun startLet() {
        lexicalScope = LexicalScope(lexicalScope)
        letExprBuilders.addFirst(LetExprBuilder(lexicalScope!!, frameDescriptor!!))
    }

    fun endLet(bodyNode: ExprNode): LetNode {
        lexicalScope = lexicalScope!!.outer
        return letExprBuilders.removeFirst().buildLetNode(bodyNode)
    }

    val currentLet: LetExprBuilder
        get() = letExprBuilders.peekFirst()

    /**
     * create the node representing variable and push it to this.nodes;
     *
     * @param name
     */
    fun createVar(name: String): ExprNode = when {
        lexicalScope!!.locals.containsKey(name) -> VariableNodeGen.create(lexicalScope!!.locals[name])
        else -> FunctionNameNode(language, name)
    }

    fun createInfix(opType: Int, left: ExprNode, right: ExprNode): BinaryNode =
        when (opType) {
            Cr01Lexer.OP_ADD -> AddNodeGen.create(left, right)
            Cr01Lexer.OP_SUB -> SubNodeGen.create(left, right)
            Cr01Lexer.OP_MUL -> MulNodeGen.create(left, right)
            Cr01Lexer.OP_DIV -> DivNodeGen.create(left, right)
            else -> error("unexpected opType\n")
        }

    fun createNumber(value: Long): LongNode = LongNode(value)

    fun createApply(funcNode: ExprNode, argNodes: Array<ExprNode>): InvokeNode = InvokeNode(funcNode, argNodes)
}

internal class LexicalScope(val outer: LexicalScope?) {
    val locals: MutableMap<String, FrameSlot>

    init {
        locals = HashMap()
        if (outer != null) {
            locals.putAll(outer.locals)
        }
    }
}

class CrFunctionBuilder internal constructor(
    private val frameDescriptor: FrameDescriptor,
    private val lexicalScope: LexicalScope,
    private val language: CrLanguage,
    private val functionName: String
) {
    private val parameterNodes = LinkedList<SimpleDeclNode>()

    fun addParameter(name: String) {
        val frameSlot = frameDescriptor.addFrameSlot(name + "#" + UUID.randomUUID())
        lexicalScope.locals[name] = frameSlot
        parameterNodes.push(SimpleDeclNodeGen.create(ReadArgumentNode(parameterNodes.size), frameSlot))
    }

    fun buildCrFunction(bodyNode: ExprNode): CrFunction = CrFunction(
        language, frameDescriptor, functionName, parameterNodes.size,
        Truffle.getRuntime().createCallTarget(
            CrRootNode(
                language,
                frameDescriptor,
                LetNode(parameterNodes.toTypedArray(), bodyNode)
            )
        )
    )

    fun buildFunctionExprNode(bodyNode: ExprNode): FunctionExprNode =
        FunctionExprNode(language, parameterNodes, bodyNode)

}

class LetExprBuilder internal constructor(
    private val lexicalScope: LexicalScope,
    private val frameDescriptor: FrameDescriptor
) {
    private val simpleDeclNodes = LinkedList<SimpleDeclNode>()
    fun addSimpleDecl(name: String, value: ExprNode) {
        val frameSlot = frameDescriptor.addFrameSlot(name + "#" + UUID.randomUUID(), FrameSlotKind.Illegal)
        lexicalScope.locals[name] = frameSlot
        simpleDeclNodes.push(SimpleDeclNodeGen.create(value, frameSlot))
    }

    fun buildLetNode(bodyNode: ExprNode): LetNode = LetNode(simpleDeclNodes.toTypedArray(), bodyNode)

}