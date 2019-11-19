package language.runtime

import language.CrLanguage
import java.util.*

class CrFunctionRegistry internal constructor(language: CrLanguage) {
    private val functions: MutableMap<String, CrFunction>
    private val language: CrLanguage
    fun lookup(name: String?): CrFunction? {
        return functions[name]
    }

    fun register(name: String, crFunction: CrFunction) {
        functions[name] = crFunction
    }

    init {
        functions = HashMap()
        this.language = language
    }
}