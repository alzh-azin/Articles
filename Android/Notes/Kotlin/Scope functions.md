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

`apply`: object configurations

```kotlin
val adam = Person("Adam").apply {
    age = 32
    city = "London"        
}
println(adam)
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


