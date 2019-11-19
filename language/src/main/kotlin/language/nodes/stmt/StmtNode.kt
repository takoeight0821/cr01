package language.nodes.stmt

import com.oracle.truffle.api.dsl.ReportPolymorphism
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.instrumentation.GenerateWrapper
import com.oracle.truffle.api.instrumentation.InstrumentableNode
import com.oracle.truffle.api.instrumentation.ProbeNode
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo

@NodeInfo(description = "The abstract base node for all statements")
@GenerateWrapper
@ReportPolymorphism
abstract class StmtNode : Node(), InstrumentableNode {
    abstract fun executeVoid(frame: VirtualFrame)
    override fun isInstrumentable(): Boolean = true
    override fun createWrapper(probe: ProbeNode?): InstrumentableNode.WrapperNode = StmtNodeWrapper(this, probe)
}