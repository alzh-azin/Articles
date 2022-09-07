# Reactive Streams on Kotlin: SharedFlow and StateFlow

## SharedFlow

A shared flow is, at its core, a *Flow*. But it has two main differences from the standard Flow implementation. It:

- Emits events even if you don’t call `collect()` on it. After all, it *is* a hot stream implementation.
- Can have multiple *subscribers*.

### Replay and Buffering

`MutableSharedFlow()` accepts three parameters:

```kotlin
public fun <T> MutableSharedFlow(
  replay: Int = 0, // 1
  extraBufferCapacity: Int = 0, // 2
  onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND // 3
): MutableSharedFlow<T>
```

1. *replay*: The number of values replayed to new subscribers. It can’t be negative and it defaults to zero.
2. *extraBufferCapacity*: The number of values buffered. It can’t be negative and it defaults to zero. The sum of this value plus `replay` comprises the total buffer of the shared flow.
3. *onBufferOverflow*: Action to take when buffer overflow is reached. It can have three values: `BufferOverflow.SUSPEND`, `BufferOverflow.DROP_OLDEST` or `BufferOverflow.DROP_LATEST`. It defaults to `BufferOverflow.SUSPEND`.

#### Default Behavior

![replay-0-extraBuffer-0.gif](../resources/replay-0-extraBuffer-0.gif)

1. This shared flow has three events and two subscribers. The first event is emitted when there are no subscribers yet, so it gets lost forever.
2. By the time the shared flow emits the second event, it already has one subscriber, which gets said event.
3. Before reaching the third event, another subscriber appears, but the first one gets suspended and remains like that until reaching the event. This means `emit()` won’t be able to deliver the third event to that subscriber. When this happens, the shared flow has two options: It either buffers the event and emits it to the suspended subscriber when it resumes or it reaches buffer overflow if there’s not enough buffer left for the event.
4. In this case, there’s a total buffer of zero — `replay + extraBufferCapacity`. In other words, buffer overflow. Because `onBufferOverflow` is set with `BufferOverflow.SUSPEND`, the *flow will suspend* until it can deliver the event to all subscribers.
5. When the subscriber resumes, so does the stream, delivering the event to all subscribers and carrying on its work.

#### With Replay

![replay-1-extraBuffer-0.gif](../resources/replay-1-extraBuffer-0.gif)

1. When the shared flow reaches the first event without any active subscribers, it doesn’t suspend anymore. With `replay = 1`, there’s now a total buffer size of one. As such, the flow buffers the first event and keeps going.
2. When it reaches the second event, there’s no more room in the buffer, so it suspends.
3. The flow remains suspended until the subscriber resumes. As soon as it does, it gets the buffered first event, along with the latest second event. The shared flow resumes, and the first event disappears forever because the second one now takes its place in the replay cache.
4. Before reaching the third event, a new subscriber appears. Due to `replay`, it also gets a copy of the latest event.
5. When the flow finally reaches the third event, both subscribers get a copy of it.
6. The shared flow buffers this third event while discarding the previous one. Later, when a third subscriber shows up, it also gets a copy of the third event.

#### With extraBufferCapacity and onBufferOverflow

The process is similar with `extraBufferCapacity`, but without the replay-like behavior. This third example shows a shared flow with both `extraBufferCapacity = 1` and `onBufferOverflow = BufferOverflow.DROP_OLDEST`:

![replay-0-extraBuffer-1.gif](../resources/replay-0-extraBuffer-1.gif)

1. The behavior is the same at first: With a suspended subscriber and a total buffer size of one, the shared flow buffers the first event.
2. The different behavior starts on the second event emission. With `onBufferOverflow = BufferOverflow.DROP_OLDEST`, the shared flow *drops the first event*, buffers the second one and carries on. Also, notice how the second subscriber *does not* get a copy of the buffered event: Remember, this shared flow has `extraBufferCapacity = 1`, but `replay = 0`.
3. The flow eventually reaches the third event, which the active subscriber receives. The flow then buffers this event, dropping the previous one.
4. Shortly after, the suspended subscriber resumes, triggering the shared flow to emit the buffered event to it and cleaning up the buffer.

### Subscribing to Event Emissions

You want the shared flow to emit no matter which screen you’re in, so you can’t bind this `ViewModel` to this specific `Fragment`. Instead, you want it bound to the `Activity` so it survives when you go from one `Fragment` to another. That’s why the code uses the `by activityViewModels` delegate.

```kotlin
private val sharedViewModel: CoinsSharedViewModel
 by activityViewModels { CoinsSharedViewModelFactory }
```

### Collecting the SharedFlow

```kotlin
viewLifecycleOwner.lifecycleScope.launchWhenStarted { // 1
  sharedViewModel.sharedViewEffects.collect { // 2
    when (it) {
      // 3
      is SharedViewEffects.PriceVariation -> notifyOfPriceVariation(it.variation)
    }
  }
}
```

1. The coroutine is scoped to the `View` instead of the `Fragment`. This ensures the coroutine is alive only while the `View` is alive, even if the `Fragment` outlives it. The code creates the coroutine with `launchWhenStarted`, instead of the most common `launch`. This way, the coroutine launches only when the lifecycle is at least in the `STARTED` state, suspends when it’s at least in the `STOPPED` state and gets canceled when the scope is destroyed. Using `launch` here can lead to potential crashes, as the coroutine will keep processing events even in the background.
2. As you can see, subscribing to a shared flow is the same as subscribing to a regular flow. The code calls `collect()` on the `SharedFlow` to subscribe to new events.
3. The subscriber reacts to the shared flow event.

### SharedFlow and Channels

Like shared flows, channels represent hot streams. But this doesn’t mean shared flow will replace the channels API — not entirely, at least. :]

`SharedFlow` is designed to completely replace `BroadcastChannel`. Not only is `SharedFlow` simpler and faster to use, but it’s a lot more versatile than `BroadcastChannel`. Keep in mind, though, that other elements from the channels API can and should still be used when it makes sense to do so.

## StateFlow

A state flow is structured like a shared flow. This is because `StateFlow` is nothing more than a *specialization* of `SharedFlow`. In fact, you can create a shared flow that behaves exactly like a state flow:

```kotlin
val shared = MutableSharedFlow(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
shared.tryEmit(InitialState()) // emit the initial value
val state = shared.distinctUntilChanged() // get StateFlow-like behavior
```

The code above creates a shared flow that emits the *latest value only* to any new subscribers. Due to that `distinctUntilChanged` at the bottom, it’ll only emit any value if it’s different from the previous one. This is exactly what a state flow does, which makes it great for holding and handling state.

### Event Emission With StateFlow

A difference worth noting between shared and state flows is event emission. You can still use `emit` and `tryEmit` with state flow, but … don’t. :]

Instead, you should do:

```kotlin
mutableState.value = newState
```

The reason is that updates to `value` are always *conflated*, which means that even if you update it faster than subscribers can consume it, they’ll get the most recent value only. One thing to keep in mind is that whatever you assign to `value` has to be a completely *different* object from whatever was there before. For instance, take this code:

```kotlin
data class State(
  var name: String = "",
  var age: Int = -1
)

val mutableState = MutableStateFlow<State>(State())

// ...

// newState and mutableState.value will reference the same object
val newState = mutableState.value 

// Reference is the same, so this is also changing mutableState.value!
newState.name = "Marc"

mutableState.value = newState
```

In this case, the state flow won’t emit the new value. Because the referenced object is the same, the equality comparison will return true, so the flow will assume it’s the same state.

To make this work, you need to use `immutable` objects. For example:

```kotlin
data class State(
  val name: String = "",
  val age: Int = -1
)

val mutableState = MutableStateFlow<State>(State())

// ...

mutableState.value = State(name = "Marc")
```

This way, the state flow will properly emit a state update. Immutability saves the day once again. :]

### StateFlow and Channels

Like `SharedFlow` can replace `BroadcastChannel` completely, `StateFlow` can replace `ConflatedBroadcastChannel` completely. There are a couple reasons for this. `StateFlow` is simpler and more efficient than `ConflatedBroadcastChannel`. It also has better distinction between mutability and immutability with `MutableStateFlow` and `StateFlow`.

#### Reference:

[Reactive Streams on Kotlin: SharedFlow and StateFlow]([Reactive Streams on Kotlin: SharedFlow and StateFlow | raywenderlich.com](https://www.raywenderlich.com/22030171-reactive-streams-on-kotlin-sharedflow-and-stateflow#toc-anchor-010))
