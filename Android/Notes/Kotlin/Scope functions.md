# Scope functions

Scope functions are functions which those pupose is executing a block of code whitin the context of an object. When you call these functions, it forms a scope which you can access the object without its name.

There are five of them: `let`, `run`, `with`, `apply` and `also`.

The benefit of using scope function are they make your code concise and readable.

`let`: execute a lambda on non-nullable objects

```kotlin
val numbers = mutableListOf("one", "two", "three", "four", "five")
numbers.map { it.length }.filter { it > 3 }.let { 
    println(it)
    // and more function calls if needed
} 
```

`with`: grouping function calls on an object

```kotlin
val numbers = mutableListOf("one", "two", "three")
with(numbers) {
    println("'with' is called with argument $this")
    println("It contains $size elements")
}
```

```kotlin
val numbers = mutableListOf("one", "two", "three")
val firstAndLast = with(numbers) {
    "The first element is ${first()}," +
    " the last element is ${last()}"
}
println(firstAndLast)
```

`run`: object configuration and computing the result

run does the same as `with` but it is an extension functions.

```kotlin
val service = MultiportService("https://example.kotlinlang.org", 80)

val result = service.run {
    port = 8080
    query(prepareRequest() + " to port $port")
}

// the same code written with let() function:
val letResult = service.let {
    it.port = 8080
    it.query(it.prepareRequest() + " to port ${it.port}")
}
```

`apply`: Object configuration

Performing some actions on properties and functions of the object.

```kotlin
val adam = Person("Adam").apply {
    age = 32
    city = "London"        
}
println(adam)
```

`also`: Additional effects

It is useful for performing some actions that take the context object as an argument. Use also for actions that need a refrence to the object rather than its properties and functions.

```kotlin
val numbers = mutableListOf("one", "two", "three")
numbers
    .also { println("The list elements before adding new one: $it") }
    .add("four")
```

> functions which have object refrences: `apply, run, with`

> functions which recieve `it` as an argument: `let, also`



#### Summary

| Functions | Object references | Return value   | Is extension function |
| --------- | ----------------- | -------------- | --------------------- |
| let       | it                | lambda result  | yes                   |
| also      | it                | context object | yes                   |
| run       | this              | lambda result  | yes                   |
| with      | this              | lambda result  | no                    |
| apply     | this              | context object | yes                   |


