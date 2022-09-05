# Substituting Android’s LiveData: StateFlow or SharedFlow?

The two main reasons for substituting `LiveData` with one of those new types are:

1. `LiveData` is closely bound to UI (no natural way to offload work to worker threads), and
2. `LiveData` is closely bound to the Android platform.

We can conclude from those two facts that, in Clean Architecture terms, while `LiveData` works fine for the Presentation Layer, it does not fit well in the Domain Layer, which should ideally be platform-independent (meaning a pure Kotlin/Java module); and it does not fit very well in the Data Layer either (Repositories implementations and Data Sources), as we usually should offload data access work to worker threads.

We could not just substitute `LiveData` with pure `Flow`, though. The main issues with using pure `Flow` as a `LiveData` substitute on *all* app layers are that:

1. `Flow` is stateless (no `.value` access).
2. `Flow` is declarative (cold): a flow-builders merely *describes* what the flow *is*, and it is only *materialized* when collected. However, a new `Flow` is effectively run (materialized) for **each** collector, meaning upstream (expensive) database access is redundantly and repeatedly run for each collector.
3. `Flow`, by itself, does not know anything about Android lifecycles, and does not provide automatic pausing and resuming of collectors upon Android lifecycle state changes.

For (3), we could already use `LifecycleCoroutineScope` extensions such as `launchWhenStarted` for launching coroutines to collect our flows — those collectors will automatically be paused and resumed in sync with the component's Lifecycle.

Now, `SharedFlow` and `StateFlow` provide a solution for those issues.

### What are the issues with using Flow in the View Layer?

The first problem with this approach is the handling of the `Lifecycle`, which `LiveData` does automatically for us. We achieved a similar behavior through the use of `launchWhenStarted {}` in the example above.
