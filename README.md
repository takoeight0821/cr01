# CR01

A toy programming language built on Truffle framework(https://github.com/oracle/graal/tree/master/truffle).

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