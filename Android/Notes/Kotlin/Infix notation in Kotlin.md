# Infix notation in Kotlin

Infix notations allows a function to be called without dot and parantheses.

```kotlin
infix fun Int.add(value: Int): Int = this + value
```

```kotlin
val sum = 5 add 10
```

`to` function is an infix function we use when we want to create a map.

```kotlin
infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
```

```kotlin
val map = mapOf(
    1 to "Amit Shekhar",
    2 to "Anand Gaurav",
    3 to "Lionel Messi"
)
```

There are some rules when you want to use infix functions:

- They must be member functions or extention functions

- They must have one input parameter

- They must not have a default value.

- They must not accept variable number of parameters. It's not acceptable to use `vararg`

- 
