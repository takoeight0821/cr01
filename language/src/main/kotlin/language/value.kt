package language.value

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.interop.ArityException
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.library.ExportLibrary
import com.oracle.truffle.api.library.ExportMessage
import com.oracle.truffle.api.nodes.DirectCallNode
import language.CrLanguage

@ExportLibrary(InteropLibrary::class)
class CrFunction @JvmOverloads constructor(
    private val language: CrLanguage,
    private val frameDescriptor: FrameDescriptor,
    val name: String,
    private val parameterCount: Int,
    val callTarget: RootCallTarget,
    private val appliedArguments: Array<Any> = arrayOf()
) : TruffleObject, Cloneable {

    private val callNode: DirectCallNode =
        DirectCallNode.create(callTarget)

    private fun arity(): Int = parameterCount - appliedArguments.size

    private fun partialApply(args: Array<Any>): CrFunction =
        CrFunction(
            language,
            frameDescriptor,
            name,
            parameterCount,
            callTarget,
            arrayOf(*appliedArguments, *args)
        )

    override fun toString(): String = name + appliedArguments + ":" + arity()

    @get:ExportMessage
    val isExecutable: Boolean
        get() = true

    @ExportMessage
    @Throws(ArityException::class)
    fun execute(arguments: Array<Any>): Any = when {
        arity() < arguments.size -> {
            CompilerDirectives.transferToInterpreter()
            throw ArityException.create(arity(), arguments.size)
        }
        arity() > arguments.size -> partialApply(arguments)
        else -> callNode.call(*this.appliedArguments, *arguments)
    }

}

@ExportLibrary(InteropLibrary::class)
@SuppressWarnings("static-method")
object CrNull : TruffleObject {
    override fun toString(): String = "NULL"

    @ExportMessage
    fun isNull(): Boolean = true
}