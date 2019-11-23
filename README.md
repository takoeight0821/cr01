# CR01

A toy programming language built on Truffle framework(https://github.com/oracle/graal/tree/master/truffle).

I recommend you to start reading [here](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/CrLanguage.kt)

## Code structure

* [language/src/main/kotlin/language](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language) Main parts of interpreter
  * [CrLanguage.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/CrLanguage.kt) This file defines CR01 language on Truffle. CrLanguage.parse is the *entrypoint* of this interpreter
  * [value.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/value.kt) This file defines representation of some data types in CR01. Currently, CrFunction(function value) and CrNull(null) are defined.
  * [Cr01ParseTreeListener.kt](https://github.com/takoeight0821/cr01/blob/master/language/src/main/kotlin/language/Cr01ParseTreeListener.kt) This file defines parse tree listener that convert ANTLR parse tree to AST defined in [language.nodes](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/nodes)
  * [nodes](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/nodes) Definitions of AST node and it's evaluator
  * [runtime](https://github.com/takoeight0821/cr01/tree/master/language/src/main/kotlin/language/runtime) implementation of runtime system (e.g. standard I/O, *lookup* definition of toplevel function)

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