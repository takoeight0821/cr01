package language.nodes

import com.oracle.truffle.api.dsl.TypeSystem
import language.value.CrFunction

@TypeSystem(Long::class, Boolean::class, CrFunction::class)
abstract class CrTypes