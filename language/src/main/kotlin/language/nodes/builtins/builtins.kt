package language.nodes.builtins

import com.oracle.truffle.api.dsl.NodeChild
import language.nodes.expr.ExprNode

@NodeChild(value = "arguments", type = Array<ExprNode>::class)
abstract class BuiltinNode : ExprNode()

