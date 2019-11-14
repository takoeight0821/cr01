package language.runtime;


import com.oracle.truffle.api.RootCallTarget;
import language.CrLanguage;

import java.util.HashMap;
import java.util.Map;

public final class CrFunctionRegistry {

    private final Map<String, CrFunction> functions;
    private final CrLanguage language;

    public CrFunctionRegistry(CrLanguage language) {
        this.functions = new HashMap<>();
        this.language = language;
    }

    public CrFunction lookup(String name) {
        return functions.get(name);
    }

    public void register(String name, RootCallTarget rootCallTarget) {
        CrFunction crFunction = new CrFunction(name, rootCallTarget);
        functions.put(name, crFunction);
    }
}
