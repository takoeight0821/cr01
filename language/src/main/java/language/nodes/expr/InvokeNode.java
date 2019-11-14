package language.nodes.expr;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import language.runtime.CrUndefinedNameException;

import java.util.Arrays;

@NodeInfo(shortName = "invoke")
public final class InvokeNode extends ExprNode {
    @Child private ExprNode functionNode;
    @Children private final ExprNode[] argumentNodes;
    @Child private InteropLibrary library;

    public InvokeNode(ExprNode functionNode, ExprNode[] argumentNodes) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object function = functionNode.executeGeneric(frame);

        /*
         * The number of arguments is constant for one invoke node. During compilation, the loop is
         * unrolled and the execute methods of all arguments are inlined. This is triggered by the
         * ExplodeLoop annotation on the method. The compiler assertion below illustrates that the
         * array length is really constant.
         * Ref: https://github.com/graalvm/simplelanguage/blob/43a85104fcda80f6a5f8f47f32c7e188e97ff6ba/language/src/main/java/com/oracle/truffle/sl/nodes/expression/SLInvokeNode.java#L82
         */
        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] argumentValues = Arrays.stream(argumentNodes).map((node) -> { return node.executeGeneric(frame); }).toArray();

        try {
            return library.execute(function, argumentValues);
        } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
            throw CrUndefinedNameException.undefinedFunction(this, function);
        }
    }
}
