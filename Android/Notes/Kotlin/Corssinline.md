# Corssinline in kotlin

Sometimes, we need to avoid "non-local returns" in lambda functions.

##### What is "non-local returns" ?

Consider we have a function with a lambda input parameter, when we want to pass lambda to that function, we add return statement to that lambda function body, so code statements below the return statement, will not be executed.

```kotlin
fun guide() {
    print("guide start")
    teach {
        print("teach")
        return
    }
    print("guide end")
}

inline fun teach(abc: () -> Unit) {
    abc()
}
```

The decompiled code is as below:

```kotlin
public void guide() {
    System.out.print("guide start");
    System.out.print("teach");
}
```

As you see, the "guide ended" string was not executed because `return` statement was executed before that.

To avoid "non-local returns", we can use `crossinline` keyword, then it will not allow us to put the return inside that lambda.

```kotlin
fun guide() {
    print("guide start")
    teach {
        print("teach")
        // return is not allowed here
    }
    print("guide end")
}

inline fun teach(crossinline abc: () -> Unit) {
    abc()
}
```


