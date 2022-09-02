# Crash Course Kotlin Flows, Channels and Shared/State Flows

# Flows

The Flow starts emitting data when `collect()` is called on the stream

You can subscribe to the flow object or you can perform operations on the flow objects which then return another flow object. Because `a flow starts producing data when you start collecting them` we can conclude that **flows are *usually* cold!** This means, that a flow only emits data when it is collected (or consumed).

 ***— A flow can now also be a hot flow —***

```kotlin
// This will keep the flow alive
// as long as the attached scope is not canceled. 
//You can basically re-use that flow and keep emitting items.
// In other words, shareIn transforms that flow into a SharedFlow,
// yet the type reflected by the API can be a simple `Flow`
myNormalFlow
.shareIn(coroutineScope, SharingStarted.Lazily)
```

***and this will transform a cold flow into a hot flow (a SharedFlow), because it can now emit as long as the attached coroutine scope is alive. That’s why we can not conclude anymore that when an API returns a flow, that it really is a cold flow.***

So we just talked about flow, but what the heck are those things called channels and why do we need them? `Both can be used for streams`, `a flow emits where a channel can receive and emit data`. The main difference between flows and channels is this: **Flows are *usually* cold** and **channels are hot**. In other words, `when using a flow the data is produced within the stream `while `in channels the data is produced outside of the stream.`



Here’s an example of a cold stream using flow

```kotlin
val dataFlow = flow {
    // code block is only executed when flow
    // is being collected
    val data = dataSource.fetchData()
    emit(data)
}
...
dataFlow.collect { ... }
```



And here’s the same with a channel

```kotlin
// the channel lives and can receive and send
// data
val dataChannel = Channel<Data>()

suspend fun fetchData() {
    val data = dataSource.fetchData()
    dataChannel.send(data)
}
```

One can call `receive()` on the channel to obtain the data that was sent through the channel by another suspended function. A channel is like a queue, it can receive data and it can be then consumed by another component. Additionally, a channel can contain a buffer and can block itself from receiving or sending further data. A channel buffers the data until someone calls `receive()` on it. A channel can continuously consume data until `close` gets called on it. There are many forms of Channels such as `BroadcastChannel` and `ConflatedBroadcastChannel` that support different but similar use cases and have the same concept. A regular `Channel` can only have one active listener / consumer at the same time, unlike`BroadcastChannel` which supports multiple listeners at once. Also, channels have a more complex API and are considered as low-level primitives, if you’re in need of a hot flow then consider using a (Mutable) Shared/StateFlow.

# MutableSharedFlow / MutableStateFlow

There are these new things called `MutableSharedFlow` and `MutableStateFlow` . Don’t be fooled by the naming, even though they contain`Flow` in their name, they are unlike the original flows, hot streams. They were introduced to deprecate `BroadcastChannel` as their API is way simpler. These Flows can live without having an active consumer, in other words, the data is produced outside of the stream and then passed to the flow, which is a clear indicator that we’re having a hot stream here.

```kotlin
// Constructor takes no value
val mySharedFlow = MutableSharedFlow<Int>()
// Constructor takes a value
val myStateFlow = MutableStateFlow<Int>(0)
...
mySharedFlow.emit(1)
myStateFlow.emit(1)
```

As you see, the main difference between a SharedFlow and a StateFlow is that a `StateFlow takes a default value through the constructor and emits it immediately when someone starts collecting`, while `a SharedFlow takes no value and emits nothing by default.` This is a very important difference as this forces you to pay attention whether you model your data as a cold/hot stream and` whether your data is a state (use StateFlow then)` or an` event (use SharedFlow).` As an example, a state could be the visibility of your UI component and it always has a value (shown/hidden) while an event can only be triggered if and only if one or multiple preconditions are fulfilled.

## State vs Event

To have a better understanding of when to use MutableSharedFlow and when to use MutableStateFlow let’s look at following scenario:

1. User clicks a button
2. Remote data is fetched
3. User gets navigated to another screen

We could model the navigation (3.) now as a state or as an event. Let’s model the navigation as a **state** first. For this we’ll assume that there is a general `NavigationState` class, an `EmptyNavigationState` and `ViewNavigationState` and a `MutableStateFlow` that emits the state

```kotlin
// A state always has a value, in this case it is the empty state
val navigationState = MutableStateFlow<NavigationState>(EmptyNavigationState())
// ... User button click happened, remote data is fetched, and now 
// we want to update the state. 

navigationState.emit(ViewNavigationState(...navigation arguments...))
```

When `navigationState` first gets collected then the consumer will receive an `EmptyNavigationState` object by default and later on a `ViewNavigationState`, it is to the consumer to handle now proper state updates (Usually within in a big `when{..}` statement)



Now we’ll model the same scenario only with navigation as an **event**



```kotlin
// No default value, as this is an event
val navigationEvent: MutableSharedFlow()
// ... User button click happened, remote data is fetched, and now 
// we want to emit an event
navigationEvent.emit(NavigationTarget(..arguments))
```

Whoever collects the flow will only receive a value when the User clicked a button and remote data is successfully fetched. We don’t have to write that big `when` block anymore to handle multiple state updates, such as the default `EmptyNavigationState` . Since we’re still using a hot flow, MVVM (Model-View-ViewModel) is a common architecture pattern, as we’re emitting values and don’t know anything about the consumer or even if there is one.


