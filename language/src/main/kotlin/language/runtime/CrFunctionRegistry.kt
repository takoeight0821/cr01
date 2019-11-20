package language.runtime

import java.util.*

class CrFunctionRegistry {
    private val functions: MutableMap<String, CrFunction> = HashMap()
    fun lookup(name: String): CrFunction? = functions[name]

    fun register(name: String, crFunction: CrFunction) {
        functions[name] = crFunction
    }
}