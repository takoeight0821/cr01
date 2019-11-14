package language.runtime;

import com.oracle.truffle.api.nodes.NodeInfo;

public class CrContext {
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
