# Suspending vs Blocking in Kotlin Coroutine

### Suspending:

When you use suspending functions, it suspend the dispatcher that the task is executed therefore dispatcher is free to execute another coroutine

### Blocking:

When you use runBlocking, it blocks the current thread and no other tasks  in another duspatchers or threads are allowed to be executed.
