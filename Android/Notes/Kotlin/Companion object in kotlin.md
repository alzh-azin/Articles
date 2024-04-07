# Companion object in kotlin

What is the kotlin companion object in java?

We use `static` keyword in Java.

In Java, if you want to access a property or method without creating an object, we use `static` keyword.

```kotlin
public class Mentor {

    public static void guide() {

    }
}
```

```kotlin
Mentor.guide();
```

### Companion object in kotlin

In kotlin, when we want to use a property or call a method without creating an object, we can use `companion object`

```kotlin
class Mentor {

    companion object {
        fun guide() { }
    }
}
```

We can also name our companion object as below:

```kotlin
class Mentor {

    companion object Config {

        const val MAX_SLOTS = 10

    }
}
```

We can access the variable with or without refrence the name:

```kotlin
val maxSlots = Mentor.MAX_SLOTS


val maxSlots = Mentor.Config.MAX_SLOTS
```

But the companion object reference name is redundant.

Important notes about companion object:

- It cannot be used outside a class.

- The companion object is common in all instances of the class.

- It is instantiated when the container class is loaded for the first time.

> Why we use `companion object` when we already have a regular `object` in kotlin?

We can define `object` outside the class

```kotlin
object Config {

    const val MAX_SLOTS = 10

}
```

Defining inside the class

```kotlin
class Mentor {

    object Config {

        const val MAX_SLOTS = 10

    }

}
```

It can be accessed as below

```kotlin
val maxSlots = Config.MAX_SLOTS // Outside the class

val maxSlots = Mentor.Config.MAX_SLOTS // Inside the class
```

Differences between `companion object` and `object`

| companion object                                                                                                                                  | object                                                                |
|:------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- |
| It needs to be defined inside a class                                                                                                             | It can be defined anywhere                                            |
| The companion object instantiated for the first time as soon as the container class is initialized even if we have not used the companion object. | The object is initialized lazily when we access it for the first time |
| It is equivalant to `static` keyword in Java.                                                                                                     | It is used for providing singleton behavior.                          |
| It is optional to named or not.                                                                                                                   | Must be named by us.                                                  |


