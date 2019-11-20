package launcher

import org.graalvm.polyglot.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.stream.Collectors

object App {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val ctx = Context.create("cr01")
        val br = BufferedReader(InputStreamReader(System.`in`))
        val src = br.lines().collect(Collectors.joining())
        println(ctx.eval("cr01", src))
    }
}