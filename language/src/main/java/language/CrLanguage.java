package language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.RootNode;
import language.nodes.AddNodeGen;
import language.nodes.CrRootNode;
import language.nodes.LongNode;
import language.runtime.CrContext;
import org.graalvm.polyglot.Context;

@TruffleLanguage.Registration(name = "cr01", id = "cr01",
        defaultMimeType = CrLanguage.MIME, characterMimeTypes = CrLanguage.MIME)
public class CrLanguage extends TruffleLanguage<CrContext>{
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
    protected CallTarget parse(ParsingRequest request) throws Exception {
        String text = request.getSource().getCharacters().toString();
        long value = Long.parseLong(text);
        CrRootNode node = new CrRootNode(this, new FrameDescriptor(), AddNodeGen.create(new LongNode(value), new LongNode(value)));
        return Truffle.getRuntime().createCallTarget(node);
    }
    
    @Override
    protected CrContext createContext(Env env) {
        return new CrContext();
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
