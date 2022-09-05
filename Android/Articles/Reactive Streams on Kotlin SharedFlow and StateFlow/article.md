# Reactive Streams on Kotlin: SharedFlow and StateFlow

## SharedFlow

A shared flow is, at its core, a *Flow*. But it has two main differences from the standard Flow implementation. It:

- Emits events even if you don’t call `collect()` on it. After all, it *is* a hot stream implementation.
- Can have multiple *subscribers*.
