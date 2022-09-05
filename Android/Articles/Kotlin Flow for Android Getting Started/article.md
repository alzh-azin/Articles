# Kotlin Flow for Android: Getting Started

### Channel

Streaming APIs are almost the exact opposite of REST APIs. When communicating with a REST API, you make a request and the API sends a response. A streaming API works differently. It connects to a client and continuously listens to new information, over time. Twitter, for example, provides a streaming API that you can use to stream tweets in real time.

You can use `Sequence`s for synchronous streams. But you need a different solution for asynchronous streams.

For asynchronous streams, you could use `Channel`s from Kotlin Coroutines. Conceptually, you can think of channels as pipes. You send items through one pipe and receive a response through the other. However, a channel represents a *hot* stream of values. Once again, hot streams start producing values immediately.

And this introduces another set of challenges.

### Hot Versus Cold Streams

A channel, which is a hot stream, will produce values even if aren’t listening to them on the other side. And if you are not listening to the stream, you are losing values.

In practice, you can use a channel to have an open network connection. But that can lead to memory leaks. Or you could forget to subscribe to a channel, and “lose” values.

Hot streams push values even when there is no one consuming them. However, *cold streams*, start pushing values only when you start collecting!

And *Kotlin Flow* is an implementation of cold streams, powered by Kotlin Coroutines!

## Kotlin Flow Basics

Flow is a stream that produces values asynchronously. Furthermore, Flow uses coroutines internally. And because of this, it enjoys all the perks of *structured concurrency*.

With structured concurrency, coroutines live for a limited amount of time. This time is connected to the `CoroutineScope` you start your coroutines in.

When you cancel the scope, you also release any running coroutines. The same rules apply to Kotlin Flow as well. When you cancel the scope, you also dispose of the Flow. You don’t have to free up memory manually! :]

There are some similarities between Kotlin Flow, LiveData and RxJava. All of them provide a way to implement the *observer pattern* in your code.

- *LiveData* is a simple observable data holder. It’s best used to store UI state, such as lists of items. It’s easy to learn and work with. But it doesn’t provide much more than that .
- *RxJava* is a very powerful tool for reactive streams. It has many features and a plethora of transformation operators. But it has a steep learning curve!
- *Flow* falls somewhere in between LiveData and RxJava. It’s very powerful but also very easy to use! The Flow API even looks a lot like RxJava!

### Flow Builders

```kotlin
val namesFlow = flow {
  val names = listOf("Jody", "Steve", "Lance", "Joe")
  for (name in names) {
    delay(100)
    emit(name)
  }
}
```

### Flow Operators

#### Intermediate Operators

```kotlin
fun main() = runBlocking {
  namesFlow
      .map { name -> name.length }
      .filter { length -> length < 5 }

  println()
}
```

Here, you used the Flow of names from earlier and you applied two intermediate operators to it:

- `map` transforms each value to another value. Here you transformed name values to their length.
- `filter` selects values that meet a condition. Here you chose values that are less than five.

The important thing to notice here is the block of code inside each of these operators. These blocks of code can call suspending functions! So you can also delay within these blocks. Or you can call other suspending functions!

Intermediate operators are cold. When you invoke an intermediate operation on a Flow, the operation is not executed immediately. Instead, you return the transformed Flow, which is still cold. The operations execute only when you invoke a terminal operator on the final stream.

#### Terminal Operators

Because Flows are cold, they won't produce values until a terminal operator is called. Terminal operators are suspending functions that start the *collection* of the flow. When you invoke a terminal operator, you invoke all the intermediate operators along with it:

```kotlin
fun main() = runBlocking {
  namesFlow
      .map { name -> name.length }
      .filter { length -> length < 5 }
      .collect { println(it) }

  println()
}
```

Since `collect()` is a suspending function, it can only be called from a coroutine or another suspending function. This is why you wrap the code with `runBlocking()`.

## Flow on Android

#### Context Preservation and Backpressure

The collection of a Flow always happens in the context of the parent coroutine. This property of Flow is called *context preservation*. But you can still change the context when emitting items. To change the context of emissions you can use `flowOn()`.

You could have a scenario in which the Flow produces events faster than the collector can consume them. In reactive streams, this is called *backpressure*. Kotlin Flow supports backpressure out of the box since it's based on coroutines. When the consumer is in a suspended state or is busy doing some work, the producer will recognize that. It will not produce any items during this time.

### Exceptions

Flow streams can complete with an exception if an emitter or code inside the operators throws an exception. `catch()` blocks handle exceptions within Flows. You can do this *imperatively* or *declaratively*. A `try-catch` block on the collector's side is an example of an *imperative* approach.

It's imperative because these catch any exceptions that occur in the emitter or in any of the operators.

You can use `catch()` to handle errors *declaratively* instead. Declarative here means you *declare* the function to handle errors. And you declare it within the Flow itself, and not a `try-catch` block.

```kotlin
val forecasts: LiveData<List<ForecastViewState>> = weatherRepository
    .getForecasts()
    .catch {
      // Log Error
    }
    .map {
      homeViewStateMapper.mapForecastsToViewState(it)
      throw Exception()
    }
    .asLiveData()
```

Build and run the app. You'll notice that the app crashes! `catch()` catches only upstream exceptions. That is, it catches exceptions from all the operators above the catch. `catch()` doesn't catch any exception that occurs after the operator.

Now move `catch()` below `map()`:

```kotlin
val forecasts: LiveData<List<ForecastViewState>> = weatherRepository
    .getForecasts()
    .map {
      homeViewStateMapper.mapForecastsToViewState(it)
      throw Exception()
    }
    .catch {
      // Log Error
    }
    .asLiveData()
```

## Searching Locations

n *HomeActivity.kt*, you already have a listener attached to the search view. When the user changes query text, the app sends the new value to `queryChannel` in *HomeViewModel.kt*. *HomeViewModel.kt* uses a `BroadcastChannel` as a bridge to pass the text from the view to the ViewModel. `offer()` passes the text and synchronously adds the specified element to the channel.

### Querying Locations

```kotlin
private val _locations = queryChannel
    //1
    .asFlow()
    //2
    .debounce(SEARCH_DELAY_MILLIS)
    //3
    .mapLatest {
      if (it.length >= MIN_QUERY_LENGTH) {
        getLocations(it)
      } else {
        emptyList()
      }
    }
    //4
    .catch {
      // Log Error
    }
```

Here's what happens in this block of code:

1. First, the call to `asFlow` converts the `Channel` into a `Flow`.
2. Next, `debounce()` waits for values to stop arriving for a given time period. This is used to avoid processing every single letter typed by users. Users usually type several letters in a row. You don't need to make a network request until the user stops typing. This ensures that you're performing the API call only after 500 milliseconds have passed with no typing!
3. Then, `mapLatest()` performs the API call and returns location results. If the original flow emits a new value while the previous API call is still in progress, `mapLatest()` ensures that computation of the previous block is canceled. `mapLatest()` performs the API call only if the search query contains at least two characters.
4. Finally, `catch()` handles errors.

Add `locations` to *HomeViewModel.kt*. This allows you to observe from the activity:

Here you're using `asLiveData()` to collect values from the origin flow and add transform them to a `LiveData` instance.
