package language.nodes.expr

import com.oracle.truffle.api.CompilerAsserts
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.interop.ArityException
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.interop.UnsupportedMessageException
import com.oracle.truffle.api.interop.UnsupportedTypeException
import com.oracle.truffle.api.nodes.ExplodeLoop
import com.oracle.truffle.api.nodes.NodeInfo
import language.runtime.CrException.Companion.typeError

@NodeInfo(shortName = "invoke")
class InvokeNode(
    @field:Child private var functionNode: ExprNode,
    @field:Children private val argumentNodes: Array<ExprNode>
) :
    ExprNode() {
    @Child
    private var library: InteropLibrary = InteropLibrary.getFactory().createDispatched(3)

    @ExplodeLoop
    override fun executeGeneric(frame: VirtualFrame): Any {
        val function = functionNode.executeGeneric(frame)
        /*
         * The number of arguments is constant for one invoke node. During compilation, the loop is
         * unrolled and the execute methods of all arguments are inlined. This is triggered by the
         * ExplodeLoop annotation on the method. The compiler assertion below illustrates that the
         * array length is really constant.
         * Ref: https://github.com/graalvm/simplelanguage/blob/43a85104fcda80f6a5f8f47f32c7e188e97ff6ba/language/src/main/java/com/oracle/truffle/sl/nodes/expression/SLInvokeNode.java#L82
         */CompilerAsserts.compilationConstant<Any>(argumentNodes.size)
        val argumentValues = argumentNodes.map { it.executeGeneric(frame) }
        return invoke(function, argumentValues)
    }

    private fun invoke(function: Any, arguments: List<Any>): Any {
        return try {
            library.execute(function, *arguments.toTypedArray())
        } catch (e: ArityException) {
            assert(e.expectedArity < e.actualArity)
            val expectedArguments = arguments.subList(0, e.expectedArity)
            val restArguments = arguments.subList(e.expectedArity, arguments.size)
            val function1 = invoke(function, expectedArguments)
            invoke(function1, restArguments)
        } catch (e: UnsupportedTypeException) {
            throw typeError(this, function, *arguments.toTypedArray())
        } catch (e: UnsupportedMessageException) {
            throw typeError(this, function, *arguments.toTypedArray())
        }
    }

}