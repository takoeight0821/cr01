package language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;

public final class CrUndefinedNameException extends CrException {
    private CrUndefinedNameException(String message, Node location) {
        super(message, location);
    }

    @CompilerDirectives.TruffleBoundary
    public static CrUndefinedNameException undefinedFunction(Node location, Object name) {
        throw new CrUndefinedNameException("Undefined function: " + name, location);
    }
}
