# Is it possible to force Garbage Collection in Android?

```kotlin
java.lang.outOfMemoryError
```

This exception caused by memory leak and sometimes we write `System.gc()` to free up the memory but this is a wrong approach. Instead, we should try to find the exact problem that leads to memory leak.

In any case, we couldn't force garbage collector to be executed. When we use `System.gc()` we can only hint the system that we need the garbage collection but it is totally up to the system whether it will honor that request or not. So, jvm only executes garbage collection whenever it wants.
