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
