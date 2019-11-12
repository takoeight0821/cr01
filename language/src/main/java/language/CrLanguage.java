package language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import org.graalvm.polyglot.Context;

@TruffleLanguage.Registration(name = "cr01", id = "cr01",
        defaultMimeType = CrLanguage.MIME, characterMimeTypes = CrLanguage.MIME)
public class CrLanguage extends TruffleLanguage<Object>{
    static final String MIME = "application/x-cr01";

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        String text = request.getSource().getCharacters().toString();
        return Truffle.getRuntime().createCallTarget(new RootNode(this){
            @Override
            public Object execute(VirtualFrame frame) {
                return "Hello " + text;
            }
        });
    }
    
    @Override
    protected Object createContext(Env env) {
        return new Object();
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
