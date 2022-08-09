# Chapter 6: Building Features - Animals Near You

### What is a presentation layer

The presentation layer encapsulates all the code related to the UI, holding all the 
UI-related components. In other words, `this layer deals with framework code.`

You can test Android UI with instrumented tests and Espresso. These tests need to 
run on a device, which makes them slow compared to unit tests.

For those reasons, `it’s a good idea to make the UI as dumb as possible.` You should strive to keep any logic unrelated to the UI decoupled from it. It’s a good thing you have a domain layer. :]

### Lifecycles of UI components

Android UI components have their own individual lifecycles. Picture an Activity
hosting a Fragment. The system can destroy and recreate that Fragment multiple 
times throughout the Activity’s lifetime. At the same time, that Fragment’s View
can be destroyed and recreated multiple times while the Fragment lives on.

Juggling different lifecycles can be daunting. For instance, `imagine that you have a Fragment that calls postDelayed on a local Handler, and you forget to remove the callbacks from the Handler in the Fragment’s onDestroy(). This might cause a memory leak, as the garbage collector can’t clean up the Fragment because something still references it.`

### Making your life easier with architecture

You won’t exactly follow an MVI pattern in this chapter. Instead, you’ll do something simpler, somewhere between MVVM and MVI. The Android community likes to call it a `unidirectional data flow architecture`. Here’s a high-level view, where the black arrows represent data flow and the open arrow represents inheritance:

![Capture2.PNG](.\resources\Capture2.PNG)

### Building animals near you

As with data binding, there’s one thing to remember when using view binding in a 
Fragment: `Fragments can outlive their Views`. So you need to clear up the binding
in the Fragment’s onDestroyView

```kotlin
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

### Creating the UI components

```kotlin
private fun setupUI() {
    val adapter = createAdapter() // 3
    setupRecyclerView(adapter)
}

```

There’s a good reason why the adapter value only exists in setupUI()’s scope. 
`Having an Adapter as a property of a Fragment is a known way of leaking the 
RecyclerView.`
That’s because, when the View is destroyed, the RecyclerView is destroyed along 
with it. But if the Fragment references the Adapter, the garbage collector won’t be 
able to collect the RecyclerView instance because `Adapter s and RecyclerViews 
have a circular dependency. In other words, they reference each other`

### Making an adapter a property of a fragment

If you need the Adapter as a property of a Fragment, don’t forget to either:

1. Null out the Adapter property in onDestroyView.

2. Null out the Adapter reference in the RecyclerView itself, before doing the same 
   for the binding.

```kotlin

```
