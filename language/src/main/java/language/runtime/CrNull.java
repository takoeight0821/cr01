package language.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
@SuppressWarnings("static-method")
public final class CrNull implements TruffleObject {
    public static final CrNull SINGLETON = new CrNull();

    private CrNull() {
    }

    @Override
    public String toString() {
        return "NULL";
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }
}
