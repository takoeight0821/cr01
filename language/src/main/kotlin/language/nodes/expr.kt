package language.nodes.expr

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import com.oracle.truffle.api.dsl.*
import com.oracle.truffle.api.frame.FrameSlot
import com.oracle.truffle.api.frame.FrameSlotKind
import com.oracle.truffle.api.frame.FrameUtil
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.instrumentation.GenerateWrapper
import com.oracle.truffle.api.instrumentation.InstrumentableNode
import com.oracle.truffle.api.instrumentation.ProbeNode
import com.oracle.truffle.api.interop.ArityException
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.interop.UnsupportedMessageException
import com.oracle.truffle.api.interop.UnsupportedTypeException
import com.oracle.truffle.api.nodes.ExplodeLoop
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.api.nodes.UnexpectedResultException
import com.oracle.truffle.api.profiles.BranchProfile
import com.oracle.truffle.api.profiles.ConditionProfile
import language.CrLanguage
import language.nodes.CrRootNode
import language.nodes.CrTypes
import language.nodes.CrTypesGen
import language.nodes.stmt.SimpleDeclNode
import language.runtime.CrContext
import language.runtime.CrException
import language.value.CrFunction
import language.value.CrNull
import java.util.*

@TypeSystemReference(CrTypes::class)
@NodeInfo(description = "The abstract base node for all expressions")
@GenerateWrapper
@ReportPolymorphism
abstract class ExprNode : Node(), InstrumentableNode {
    abstract fun executeGeneric(frame: VirtualFrame): Any

    @Throws(UnexpectedResultException::class)
    open fun executeLong(frame: VirtualFrame): Long = CrTypesGen.expectLong(executeGeneric(frame))

    @Throws(UnexpectedResultException::class)
    open fun executeBoolean(frame: VirtualFrame): Boolean = CrTypesGen.expectBoolean(executeGeneric(frame))

    @Throws(UnexpectedResultException::class)
    open fun executeCrFunction(frame: VirtualFrame): CrFunction = CrTypesGen.expectCrFunction(executeGeneric(frame))

    // TODO: support source section
    override fun isInstrumentable(): Boolean = true

    override fun createWrapper(probe: ProbeNode?): InstrumentableNode.WrapperNode = ExprNodeWrapper(this, probe)
}

// Literal
@NodeInfo(shortName = "value")
class LongNode(private val value: Long) : ExprNode() {
    override fun executeLong(frame: VirtualFrame): Long = value

    override fun executeGeneric(frame: VirtualFrame): Any = value
}

@NodeInfo(shortName = "value")
class BoolNode(private val value: Boolean) : ExprNode() {
    override fun executeBoolean(frame: VirtualFrame): Boolean = value
    override fun executeGeneric(frame: VirtualFrame): Any = value
}

// Variable
@NodeField(name = "slot", type = FrameSlot::class)
abstract class VariableNode : ExprNode() {
    protected abstract val slot: FrameSlot
    @Specialization(guards = ["isLong(frame)"])
    fun readLong(frame: VirtualFrame): Long =
        FrameUtil.getLongSafe(frame, slot)

    @Specialization(replaces = ["readLong"])
    fun read(frame: VirtualFrame): Any =
        FrameUtil.getObjectSafe(frame, slot)

    fun isLong(frame: VirtualFrame): Boolean = frame.frameDescriptor.getFrameSlotKind(slot) == FrameSlotKind.Long
}

// Binary operator
@NodeChildren(
    NodeChild("leftNode"),
    NodeChild("rightNode")
)
sealed class BinaryNode : ExprNode() {
    abstract val leftNode: ExprNode
    abstract val rightNode: ExprNode

    @NodeInfo(shortName = "+")
    abstract class AddNode : BinaryNode() {
        @Specialization
        fun add(left: Long, right: Long): Long = left + right

        @Fallback
        fun typeError(left: Any, right: Any): Any = throw CrException.typeError(this, left, right)
    }

    @NodeInfo(shortName = "-")
    abstract class SubNode : BinaryNode() {
        @Specialization
        fun sub(left: Long, right: Long): Long = left - right

        @Fallback
        fun typeError(left: Any, right: Any): Any = throw CrException.typeError(this, left, right)
    }

    @NodeInfo(shortName = "*")
    abstract class MulNode : BinaryNode() {
        @Specialization
        fun mul(left: Long, right: Long): Long = left * right

        @Fallback
        fun typeError(left: Any, right: Any): Any = throw CrException.typeError(this, left, right)
    }

    @NodeInfo(shortName = "/")
    abstract class DivNode : BinaryNode() {
        @Specialization
        fun div(left: Long, right: Long): Long = left / right

        @Fallback
        fun typeError(left: Any, right: Any): Any = throw CrException.typeError(this, left, right)
    }

    @NodeInfo(shortName = "==")
    abstract class EqNode : BinaryNode() {
        @Specialization
        fun eq(left: Any, right: Any): Boolean = left == right
    }

    @NodeInfo(shortName = "!=")
    abstract class NeNode : BinaryNode() {
        @Specialization
        fun ne(left: Any, right: Any): Boolean = left != right
    }
}

// Function literal
class FunctionExprNode(
    private val language: CrLanguage,
    private val parameterList: List<SimpleDeclNode>,
    private val bodyNode: ExprNode
) : ExprNode() {
    override fun executeCrFunction(frame: VirtualFrame): CrFunction =
        CrFunction(
            language,
            frame.frameDescriptor,
            "lambda#" + UUID.randomUUID(),
            parameterList.size,
            Truffle.getRuntime().createCallTarget(
                CrRootNode(
                    language,
                    frame.frameDescriptor,
                    LetNode(parameterList.toTypedArray(), bodyNode),
                    frame.materialize()
                )
            )
        )

    override fun executeGeneric(frame: VirtualFrame): Any = executeCrFunction(frame)
}

// Function name
@NodeInfo(shortName = "func")
class FunctionNameNode(language: CrLanguage, private val functionName: String) : ExprNode() {
    @CompilerDirectives.CompilationFinal
    private var cachedFunction: CrFunction? = null
    private val reference: TruffleLanguage.ContextReference<CrContext> = language.contextReference
    override fun executeGeneric(frame: VirtualFrame): Any = executeCrFunction(frame)

    override fun executeCrFunction(frame: VirtualFrame): CrFunction = when (cachedFunction) {
        null -> {
            CompilerDirectives.transferToInterpreterAndInvalidate()
            cachedFunction = reference.get().lookup(functionName)
            cachedFunction!!
        }
        else -> {
            cachedFunction!!
        }
    }
}

// Read arguments (used in CrFunction)
class ReadArgumentNode(private val index: Int) : ExprNode() {
    private val outOfBoundsTaken = BranchProfile.create()
    override fun executeGeneric(frame: VirtualFrame): Any {
        val args = frame.arguments
        return if (index < args.size) {
            args[index]
        } else {
            outOfBoundsTaken.enter()
            CrNull
        }
    }
}

// Function call
@NodeInfo(shortName = "invoke")
@NodeChildren(
    NodeChild("functionNode"),
    NodeChild(value = "argumentsNode", type = InvokeNode.ArgumentsNode::class)
)
abstract class InvokeNode() :
    ExprNode() {
    @Child
    var library: InteropLibrary = InteropLibrary.getFactory().createDispatched(3)

    private val isCrFunctionProfile: ConditionProfile = ConditionProfile.createCountingProfile()
    private val isActualProfile: ConditionProfile = ConditionProfile.createCountingProfile()

    @Specialization(guards = ["isActualArity(function, arguments)"])
    @ExplodeLoop
    fun invokeActual(function: CrFunction, arguments: Array<Any>): Any {
        return library.execute(function, *arguments)
    }

    @Specialization
    @ExplodeLoop
    fun invokeCurried(function: CrFunction, arguments: Array<Any>): Any {
        assert(function.arity() < arguments.size)

        val expectedArguments = arguments.copyOfRange(0, function.arity())
        val restArguments = arguments.copyOfRange(function.arity(), arguments.size)
        val function1 = invokeActual(function, expectedArguments)

        if (isCrFunctionProfile.profile(function1 is CrFunction)) {
            return if (isActualProfile.profile(isActualArity(function1 as CrFunction, restArguments))) {
                invokeActual(function1, restArguments)
            } else {
                invokeCurried(function1, restArguments)
            }
        }
        return invoke(function1, restArguments)
    }

    fun isActualArity(function: CrFunction, arguments: Array<Any>): Boolean = function.arity() >= arguments.size

    @Specialization(replaces = ["invokeActual", "invokeCurried"])
    @ExplodeLoop
    fun invoke(function: Any, arguments: Array<Any>): Any {
        assert(function !is CrFunction)
        return try {
            library.execute(function, *arguments)
        } catch (e: ArityException) {
            throw CrException.typeError(this, function, *arguments)
        } catch (e: UnsupportedTypeException) {
            throw CrException.typeError(this, function, *arguments)
        } catch (e: UnsupportedMessageException) {
            throw CrException.typeError(this, function, *arguments)
        }
    }

    @NodeInfo(shortName = "arguments")
    class ArgumentsNode(@field:Children private val argumentNodes: Array<ExprNode>) : Node() {
        fun execute(frame: VirtualFrame): Array<Any> {
            return argumentNodes.map { a -> a.executeGeneric(frame) }.toTypedArray()
        }
    }
}

// Define local variables
@NodeInfo(shortName = "let")
class LetNode(
    @field:Children private val declNodes: Array<SimpleDeclNode>,
    @field:Child private var bodyNode: ExprNode
) :
    ExprNode() {

    @ExplodeLoop
    override fun executeGeneric(frame: VirtualFrame): Any {
        for (declNode in declNodes) {
            declNode.executeVoid(frame)
        }
        return bodyNode.executeGeneric(frame)
    }
}

// if expression
@NodeInfo(shortName = "if")
class IfNode(
    @field:Child private var conditionNode: ExprNode,
    @field:Child private var thenNode: ExprNode,
    @field:Child private var elseNode: ExprNode
) : ExprNode() {
    private val condition = ConditionProfile.createCountingProfile()
    override fun executeGeneric(frame: VirtualFrame): Any = if (condition.profile(evaluateCondition(frame))) {
        thenNode.executeGeneric(frame)
    } else {
        elseNode.executeGeneric(frame)
    }

    private fun evaluateCondition(frame: VirtualFrame): Boolean = try {
        conditionNode.executeBoolean(frame);
    } catch (ex: UnexpectedResultException) {
        throw CrException.typeError(this, ex.result)
    }

}