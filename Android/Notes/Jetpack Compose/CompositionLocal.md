# CompositionLocal

CompositionLocal is a concept in JetpackCompose, which provides a way to propagate data through the compose tree, without having to pass the data through function parameters.

Here is an example of how to define a CompositionLocal

```kotlin
val LocalExample = compositionLocalOf<String> { "Default Value" }
```

Here is how you might provide a value

```kotlin
CompositionLocalProvider(LocalExample provides "New Value") {
    // Composables in this block will see "New Value" when they read from LocalExample
}
```

Here is how you might read a value

```kotlin
val exampleValue = LocalExample.current
```

> CompositionLocal does not control the lifetime of the object. It is just only a mechanism to propagate data through the compose tree.
> 
> The lifetime of an object is determined by the scope in which it is injected by hilt not by compose or CompositionLocal. Hilt takes care of providing the instance and managing its lifecycle.



### What is different between `compositionLocalOf` and `staticCompositionLocalOf` ?

Both used to create a `ProvidableCompositionLocal` but they differ in how they handle changes to the provided value

1. `compositionLocalOf`: this is used when the provided value is expected to change over time. When provided value changes, only the composables that read the value will be recomposed. It is similar to how `mutableStateOf` works.

2. `staticCompositionLocalOf`: this is used when the provided value is not expected to change, or change very infrequently. When the provided value is changes, the entire content of the `CompositionLocalProvider` will be recomposed. This makes `CompositionLocalProvider` more efficient when the provided value is highly unlikely to change.


