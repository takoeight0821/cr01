# CR01

A toy programming language built on Truffle framework(https://github.com/oracle/graal/tree/master/truffle).

# Build

```shell script
$ ./gradlew build
```

# Run

```shell script
$ echo "s x y z = x z (y z); k x y = x; main _ = s k k 42" | ./gradlew run

> Task :launcher:run
42

BUILD SUCCESSFUL in 1s
2 actionable tasks: 1 executed, 1 up-to-date
$ echo "s x y z = x z (y z); k x y = x; main _ = s k k" | ./gradlew run

> Task :launcher:run
s [k [], k []]

BUILD SUCCESSFUL in 1s
2 actionable tasks: 1 executed, 1 up-to-date
```