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


