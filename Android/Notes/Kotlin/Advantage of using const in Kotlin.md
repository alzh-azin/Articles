# Advantage of using const in Kotlin

Assume you need to declare a variable which its data will not get changed over the time. There are two ways to define that variable:

#### First Aproach:

We can define it as a regular variable:

```kotlin
object Constants {
    val NAME = "Amit"
}
```

We are accessing this NAME as below:

```kotlin
fun testValWithoutConst() {
    val name = Constants.NAME
}
```

Now we need to compile this code to undrestand the difference:

Decompiled code:

```java
public final void testValWithoutConst() {
  String name = Constants.INSTANCE.getNAME();
}
```

As above code, java needs to create an instance to access the variable which this approach has overheads in runtime.

> But what if we mark the variable as const variable?

```kotlin
object Constants {
    const val NAME = "Amit"
}
```

Decompiled code:

```java
public final void testValWithConst() {
  String name = "Amit";
}
```

As you see, the value has been inlined and there will be no overheads to access that variable at runtime.
