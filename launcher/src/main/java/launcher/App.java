package launcher;

import org.graalvm.polyglot.Context;

public class App {
    public static void main(String... args) {
        var ctx = Context.create("cr01");
        System.out.println(ctx.eval("cr01", "4 + (2 - 3) + 9"));
    }
}
