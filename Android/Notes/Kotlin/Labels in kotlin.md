# Labels in kotlin

Labels in kotlin are kind of markers that are used with statements like `break`, `continue` and `return` to control the flow of the program.

Labels are useful when you are dealing with `nested loops` and `lambda functions`.

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    numbers.forEach label@{
        if (it == 3) return@label // returns from the lambda expression, not the main function
        println(it)
    }
    println("End of main function")
}
```


