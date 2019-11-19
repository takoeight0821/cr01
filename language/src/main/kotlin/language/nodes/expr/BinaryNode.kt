package language.nodes.expr

import com.oracle.truffle.api.dsl.Fallback
import com.oracle.truffle.api.dsl.NodeChild
import com.oracle.truffle.api.dsl.NodeChildren
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.instrumentation.GenerateWrapper
import com.oracle.truffle.api.nodes.NodeInfo
import language.runtime.CrException

@NodeChildren(
    NodeChild("leftNode"),
    NodeChild("rightNode")
)
abstract class BinaryNode : ExprNode()

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