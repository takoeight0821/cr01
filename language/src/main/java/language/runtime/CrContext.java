package language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.CrLanguage;

public final class CrContext {
    private final CrFunctionRegistry functionRegistry;
    private final CrLanguage language;

    public CrContext(CrLanguage language) {
        this.language = language;
        this.functionRegistry = new CrFunctionRegistry(this.language);
    }

    /* function environment */
    public CrFunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    /*
     * Methods for language interoperability.
     */
    public static Object fromForeignValue(Object a) {
        if (a instanceof Long) {
            return a;
        } else if (a instanceof Number) {
            return fromForeignNumber(a);
        } else if (a instanceof TruffleObject) {
            return a;
        } else if (a instanceof CrContext) {
            return a;
        }
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException(a + "is not a Truffle value");
    }

    @CompilerDirectives.TruffleBoundary
    private static long fromForeignNumber(Object a) {
        return ((Number) a).longValue();
    }

    public static NodeInfo lookupNodeInfo(Class<?> aClass) {
        if (aClass == null) {
            return null;
        }
        NodeInfo info = aClass.getAnnotation(NodeInfo.class);
        if (info != null) {
            return info;
        } else {
            return lookupNodeInfo(aClass.getSuperclass());
        }
    }
}
