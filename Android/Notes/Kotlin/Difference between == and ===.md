# Difference between == and ===

There are two types of equality check in kotlin:

- Structure equality (==) : it checks for `equal()`

- Referential equality (===) : it checks whether two objects references the same address in memory.

###### About `equals()` function:

- By default, this function checks for referential equality if you use it in regular classes

- In data classes, `equals()`, `hashCode()`, `toString()` and `copy()` are automatically generated and there is no need to override.

- If you use `equals()` in a data class, it compares the value of all properties declared in primary constructor

### Structure equality (==)

###### Regular classes:

First example:

```kotlin
class Car(val color: String) {

}
```

```kotlin
val car1 = Car("RED")
val car2 = Car("BLUE")
println(car1 == car2)
```

output:

```kotlin
false
```

Second example:

```kotlin
val car1 = Car("RED")
val car2 = Car("RED")
println(car1 == car2)
```

output:

```kotlin
false
```

We get false because in regular classes `equals()` function is not overrided. And as it is mentioned previously, it checks for referential equality

###### Data classes:

```kotlin
data class Car(val color: String) {

}
```

```kotlin
val car1 = Car("RED")
val car2 = Car("RED")
println(car1 == car2)
```

output:

```kotlin
true
```

It checks whether all properties have equal values.

### Referential equality

```kotlin
val car1 = Car("RED")
val car2 = Car("RED")
println(car1 === car2)
```

```kotlin
false
```

It is because their object reference are different.

```kotlin
val car1 = Car("RED")
val car2 = car1
println(car1 === car2)
```

```kotlin
true
```
