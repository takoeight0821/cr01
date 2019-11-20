package language.runtime

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
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

@ExportLibrary(InteropLibrary::class)
class CrFunction(
    private val language: CrLanguage,
    private val frameDescriptor: FrameDescriptor,
    val name: String,
    private val parameterCount: Int,
    val callTarget: RootCallTarget
) : TruffleObject, Cloneable {

    private val callNode: DirectCallNode = DirectCallNode.create(callTarget)
    private val appliedArguments: LinkedList<Any> = LinkedList()
    private fun arity(): Int = parameterCount - appliedArguments.size

    private fun partialApply(args: List<Any>): CrFunction {
        val newFunc = CrFunction(language, frameDescriptor, name, parameterCount, callTarget)
        newFunc.appliedArguments.addAll(args)
        return newFunc
    }

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
        arity() > arguments.size -> partialApply(
            Arrays.stream(arguments).collect(
                Collectors.toList()
            )
        )
        else -> callNode.call(
            *Stream.concat(
                appliedArguments.stream(),
                Arrays.stream(arguments)
            ).toArray()
        )
    }

}