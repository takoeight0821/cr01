package language.nodes;

import com.oracle.truffle.api.dsl.TypeSystem;
import language.runtime.CrFunction;

@TypeSystem({long.class, CrFunction.class})
public abstract class CrTypes {
}
