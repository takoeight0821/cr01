package language.nodes.expr

import com.oracle.truffle.api.dsl.ReportPolymorphism
import com.oracle.truffle.api.dsl.TypeSystemReference
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.instrumentation.GenerateWrapper
import com.oracle.truffle.api.instrumentation.InstrumentableNode
import com.oracle.truffle.api.instrumentation.ProbeNode
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.api.nodes.UnexpectedResultException
import language.nodes.CrTypes
import language.nodes.CrTypesGen
import language.runtime.CrFunction


@TypeSystemReference(CrTypes::class)
@NodeInfo(description = "The abstract base node for all expressions")
@GenerateWrapper
@ReportPolymorphism
abstract class ExprNode : Node(), InstrumentableNode {
    abstract fun executeGeneric(frame: VirtualFrame): Any

    @Throws(UnexpectedResultException::class)
    open fun executeLong(frame: VirtualFrame): Long {
        return CrTypesGen.expectLong(executeGeneric(frame))
    }

    @Throws(UnexpectedResultException::class)
    open fun executeCrFunction(frame: VirtualFrame): CrFunction {
        return CrTypesGen.expectCrFunction(executeGeneric(frame))
    }

    // TODO: support source section
    override fun isInstrumentable(): Boolean = true
    override fun createWrapper(probe: ProbeNode?): InstrumentableNode.WrapperNode = ExprNodeWrapper(this, probe)
}