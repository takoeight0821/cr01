package language.runtime;


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

    public void register(String name, CrFunction crFunction) {
        functions.put(name, crFunction);
    }
}
