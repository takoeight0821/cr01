package language.nodes

import com.oracle.truffle.api.dsl.TypeSystem
import language.runtime.CrFunction

@TypeSystem(Long::class, CrFunction::class)
abstract class CrTypes