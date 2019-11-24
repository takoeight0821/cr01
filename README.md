# CR01

A toy programming language built on Truffle framework(https://github.com/oracle/graal/tree/master/truffle).

I recommend you to start reading [here](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/CrLanguage.kt)

## Code structure

* [language/src/main/kotlin/language](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language) Main parts of interpreter
  * [CrLanguage.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/CrLanguage.kt) This file defines CR01 language on Truffle. CrLanguage.parse is the *entrypoint* of this interpreter
  * [value.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/value.kt) This file defines representation of some data types in CR01. Currently, CrFunction(function value) and CrNull(null) are defined.
  * [Cr01ParseTreeListener.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/Cr01ParseTreeListener.kt) This file defines parse tree listener that convert ANTLR parse tree to AST defined in [language.nodes](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/nodes)
  * [nodes](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/nodes) Definitions of AST node and it's evaluator
    * [CrEvalRootNode.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/CrEvalRootNode.kt) Evaluation is started from this node.
    * [CrRootNode.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/CrRootNode.kt) Root node of each function body.
    * [CrTypes.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/CrTypes.kt) This file defines CR01's type system.
    * [builder.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/builder.kt) Node builder classes. These classes are used by [Cr01ParseTreeListener.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/Cr01ParseTreeListener.kt)
    * [builtins.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/builtins.kt) This file defines builtin functions.
    * [expr.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/expr.kt) This file defines expression nodes.
    * [stmt.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/nodes/stmt.kt) This file defines statement nodes.
  * [runtime](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/runtime) implementation of runtime system (e.g. standard I/O, *lookup* definition of toplevel function)
    * [CrContext.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/runtime/CrContext.kt) Global environment.
    * [CrException.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/runtime/CrException.kt) Runtime exceptions.

# Build

```shell script
$ ./gradlew build
```

# Run

```shell script
$ echo "s x y z = x z (y z); k x y = x; main _ = s k k 42" | ./gradlew run
  42
$ echo "s x y z = x z (y z); k x y = x; main _ = s k k" | ./gradlew run
  s [k [], k []]
$ echo 's x = fn y z -> x z (y z); k x = fn y -> x; main _ = s k k 42;' | ./gradlew run
  42
```