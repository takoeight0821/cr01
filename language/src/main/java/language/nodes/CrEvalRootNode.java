package language.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import language.CrLanguage;
import language.runtime.CrContext;
import language.runtime.CrFunction;
import language.runtime.CrNull;

import java.util.Arrays;
import java.util.Map;

/**
 * This class performs two additional tasks: (as SimpleLanguage https://github.com/graalvm/simplelanguage/)
 * 1. Lazily registeration of functions on first execution. This fulfills the semantics of "evaluating"
 * source code in Cr01.
 * 2. Conversion of arguments to types understood by Cr01. The Cr01 source code can be evaluated from a
 * different language, i.e., the caller can be a node from a different language that uses types not
 * understood by Cr01.
 */
public final class CrEvalRootNode extends RootNode {
    private final Map<String, CrFunction> functions;
    @Child
    private DirectCallNode mainCallNode;
    private final TruffleLanguage.ContextReference<CrContext> reference;
    @CompilationFinal
    private boolean registered;

    public CrEvalRootNode(CrLanguage language, RootCallTarget rootFunction, Map<String, CrFunction> functions) {
        super(language); // internal frame
        this.functions = functions;
        this.mainCallNode = rootFunction != null ? DirectCallNode.create(rootFunction) : null;
        this.reference = language.getContextReference();
    }

    @Override
    protected boolean isInstrumentable() {
        return false;
    }

    @Override
    public String getName() {
        return "root eval";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        /* Lazy registrations of functions on first execution. */
        if (!registered) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            functions.forEach(reference.get().getFunctionRegistry()::register);
            registered = true;
        }
        if (mainCallNode == null) {
            /* The source code did not have a main function, so nothing to execute. */
            return CrNull.SINGLETON;
        } else {
            Object[] arguments = Arrays.stream(frame.getArguments()).map(CrContext::fromForeignValue).toArray();
            return mainCallNode.call(arguments);
        }
    }
}
