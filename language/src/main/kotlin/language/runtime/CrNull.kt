package language.runtime

import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.library.ExportLibrary
import com.oracle.truffle.api.library.ExportMessage

@ExportLibrary(InteropLibrary::class)
@SuppressWarnings("static-method")
object CrNull : TruffleObject {
    override fun toString(): String = "NULL"

    @ExportMessage
    fun isNull(): Boolean = true
}