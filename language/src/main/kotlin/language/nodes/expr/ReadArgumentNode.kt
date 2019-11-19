package language.nodes.expr

import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.profiles.BranchProfile
import language.runtime.CrNull

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