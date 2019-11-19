package launcher;

import org.graalvm.polyglot.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class App {
    public static void main(String... args) throws IOException {
        Context ctx = Context.create("cr01");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String src = br.lines().collect(Collectors.joining());

        System.out.println(ctx.eval("cr01", src));
    }
}
