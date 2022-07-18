# Composing suspending functions

### Sequential by default

```kotlin
suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}
```

What do we do if we need them to be invoked *sequentially* — first `doSomethingUsefulOne` *and then* `doSomethingUsefulTwo`, and compute the sum of their results? In practice, we do this if we use the result of the first function to make a decision on whether we need to invoke the second one or to decide on how to invoke it.

```kotlin
val time = measureTimeMillis {
    val one = doSomethingUsefulOne()
    val two = doSomethingUsefulTwo()
    println("The answer is ${one + two}")
}
println("Completed in $time ms")
```

> The answer is 42

### Concurrent using async

What if there are no dependencies between invocations of `doSomethingUsefulOne` and `doSomethingUsefulTwo` and we want to get the answer faster, by doing both *concurrently*? This is where [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) comes to help.

Conceptually, [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) is just like [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html). It starts a separate coroutine which is a light-weight thread that works concurrently with all the other coroutines. The difference is that `launch` returns a [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) and does not carry any resulting value, while `async` returns a [Deferred](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html) — a light-weight non-blocking future that represents a promise to provide a result later. You can use `.await()` on a deferred value to get its eventual result, but `Deferred` is also a `Job`, so you can cancel it if needed.

```kotlin
val time = measureTimeMillis {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

> The answer is 42

This is twice as fast, because the two coroutines execute concurrently. Note that concurrency with coroutines is always explicit.

```kotlin
fun main(){
    runBlocking {
        val one = async {
            doSomethingUsefulOne()
        }
        val two = async {
            doSomethingUsefulTwo()
        }
        println(two.await())
        println(one.await())
    }
}

suspend fun doSomethingUsefulOne(): Int{
    delay(5000)
    println("called doSomethingUsefulOne")
    return 3
}

suspend fun doSomethingUsefulTwo(): Int{
    delay(1000)
    println("called doSomethingUsefulTwo")
    return 5
}
```

> called doSomethingUsefulTwo
> 5
> called doSomethingUsefulOne
> 3

1. execute one and two concurrency

2. execute two.await()

3. wait for one

4. execute one.await()

### Lazily started async

Optionally, [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) can be made lazy by setting its `start` parameter to [CoroutineStart.LAZY](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-start/-l-a-z-y/index.html). In this mode it only starts the coroutine when its result is required by [await](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/await.html), or if its `Job`'s [start](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/start.html) function is invoked. Run the following example:

```kotlin
val time = measureTimeMillis {
    val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
    val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
    // some computation
    one.start() // start the first one
    two.start() // start the second one
    println("The answer is ${one.await() + two.await()}")
}
```

So, here the two coroutines are defined but not executed as in the previous example, but the control is given to the programmer on when exactly to start the execution by calling [start](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/start.html). We first start `one`, then start `two`, and then await for the individual coroutines to finish.

Note that if we just call [await](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/await.html) in `println` without first calling [start](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/start.html) on individual coroutines, this will lead to sequential behavior, since [await](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/await.html) starts the coroutine execution and waits for its finish, which is not the intended use-case for laziness. The use-case for `async(start = CoroutineStart.LAZY)` is a replacement for the standard `lazy` function in cases when computation of the value involves suspending functions.

### Summary

- execution in the Coroutine scop is sequential unless you start the  Coroutine scope in it like async or launch

- when you call suspend function in the coroutine scope, functions execute sequentially. for example, doSomethingUsefullOne takes 5 seconds to complete the process. we wait 5 seconds then execute the next line.

- if you want your async block to start whenever you want, you can pass the `CoroutineStart.LAZY` to the `start` parameter. in this case, if you just call await on lazy async, this will lead to sequential behavior.
