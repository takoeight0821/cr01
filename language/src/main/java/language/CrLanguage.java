package language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import language.nodes.CrEvalRootNode;
import language.parser.Cr01Lexer;
import language.parser.Cr01Parser;
import language.runtime.CrContext;
import language.runtime.CrFunction;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Map;

@TruffleLanguage.Registration(name = "cr01", id = "cr01",
        defaultMimeType = CrLanguage.MIME, characterMimeTypes = CrLanguage.MIME)
public class CrLanguage extends TruffleLanguage<CrContext> {
    static final String MIME = "application/x-cr01";

    public static String toString(Object value) {
        return value.toString();
    }

    public static String getTypeInfo(Object value) {
        if (value == null) {
            return "ANY";
        }
        InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
        if (interop.isNumber(value)) {
            return "Number";
        } else if (interop.isBoolean(value)) {
            return "Boolean";
        } else if (interop.isString(value)) {
            return "String";
        } else if (interop.isNull(value)) {
            return "NULL";
        } else if (interop.isExecutable(value)) {
            return "Function";
        } else if (interop.hasMembers(value)) {
            return "Object";
        } else {
            return "Unsupported";
        }
    }

    @Override
    protected CallTarget parse(ParsingRequest request) {
        var functions = parseSource(request.getSource());
        CrFunction main = functions.get("main");
        RootNode evalMain;
        if (main != null) {
            evalMain = new CrEvalRootNode(this, main.getCallTarget(), functions);
        } else {
            evalMain = new CrEvalRootNode(this, null, functions);
        }
        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    private Map<String, CrFunction> parseSource(Source source) {
        var charStream = CharStreams.fromString(source.getCharacters().toString());
        var lexer = new Cr01Lexer(charStream);
        var parser = new Cr01Parser(new CommonTokenStream(lexer));
        var prog = parser.prog();
        var treeWalker = new ParseTreeWalker();
        var listener = new Cr01ParseTreeListener(this);
        treeWalker.walk(listener, prog);
        return listener.getFunctions();
    }

    @Override
    protected CrContext createContext(Env env) {
        return new CrContext(this);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
