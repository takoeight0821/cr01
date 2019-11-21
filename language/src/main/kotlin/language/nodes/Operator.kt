package language.nodes

interface Operator<in A, in B, out C> {
    fun apply(left: A, right: B): C
}

enum class ArithOperator: Operator<Long, Long, Long> {
    Add {
        override fun apply(left: Long, right: Long): Long = left + right
    },
    Sub {
        override fun apply(left: Long, right: Long): Long = left - right
    },
    Mul {
        override fun apply(left: Long, right: Long): Long = left * right
    },
    Div {
        override fun apply(left: Long, right: Long): Long = left / right
    }
}