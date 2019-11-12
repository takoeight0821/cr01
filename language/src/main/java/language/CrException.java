package language;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import language.runtime.CrContext;

public class CrException extends RuntimeException implements TruffleException {

    private final Node location;

    public CrException(String message, Node location) {
        super(message);
        this.location = location;
    }

    @Override
    public Node getLocation() {
        return location;
    }

    public static CrException typeError(Node operation, Object... values) {
        StringBuilder result = new StringBuilder();
        result.append("Type error");

        if (operation != null) {
            SourceSection ss = operation.getEncapsulatingSourceSection();
            if (ss != null && ss.isAvailable()) {
                result.append(" at ").append(ss.getSource().getName()).append(" line ").append(ss.getStartLine()).append(" col ").append(ss.getStartColumn());
            }
        }

        result.append(": operation");
        if (operation != null) {
            NodeInfo nodeInfo = CrContext.lookupNodeInfo(operation.getClass());
            if (nodeInfo != null) {
                result.append(" \"").append(nodeInfo.shortName()).append("\"");
            }
        }

        result.append(" not defined for");

        String sep = " ";
        for (Object value: values) {
            result.append(sep);
            sep = ", ";
            if (value == null || InteropLibrary.getFactory().getUncached().isNull(value)) {
                result.append(CrLanguage.toString(value));
            } else {
                result.append(CrLanguage.getTypeInfo(value));
                result.append(" ");
                if (InteropLibrary.getFactory().getUncached().isString(value)) {
                    result.append("\"");
                }
                result.append(CrLanguage.toString(value));
                if (InteropLibrary.getFactory().getUncached().isString(value));
                result.append("\"");
            }
        }
        return new CrException(result.toString(), operation);
    }
}
