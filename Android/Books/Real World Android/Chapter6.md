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

### Exposing the state

```kotlin
val state: LiveData<AnimalsNearYouViewState> get() = _state // 1
private val _state = MutableLiveData<AnimalsNearYouViewState>()
private var currentPage = 0 // 2
init {
    _state.value = AnimalsNearYouViewState() // 3
}

// 4
fun onEvent(event: AnimalsNearYouEvent) {
    when (event) {
        is AnimalsNearYouEvent.RequestInitialAnimalsList ->
            loadAnimals()
    }
}
```

1. LiveData exposes the state to AnimalsNearYouFragment. LiveData is a simple 
   UI data holder. You should avoid using it on any other part of your code, unless 
   you want framework code in your domain layer! Using a reactive stream here — 
   like RxJava’s BehaviorSubject, or Kotlin’s StateFlow and SharedFlow — would 
   give you more control over both emissions and subscriptions. However, LiveData
   fits in 80% of cases and gives you lifecycle-aware behavior for free. Also worth 
   mentioning is the custom getter that returns _state, a private 
   MutableLiveData. This avoids exposing a mutable state variable to the view.

2. You need to track the page you’re on to request the right data. Knowing the exact 
   page isn’t relevant for the UI state — unless it’s the last one, but that’s why you 
   have noMoreAnimalsNearby. This lets you keep this property out of the exposed 
   state

3. You set _state to the initial state value

4. You create the only public method in the ViewModel. AnimalsNearYouFragment
   calls this method whenever it has an event to trigger

### Triggering the initial API request

```kotlin
// 1
private fun loadAnimals() {
    if (state.value!!.animals.isEmpty()) { // 2
        loadNextAnimalPage()
    }
}

private fun loadNextAnimalPage() {
    val errorMessage = "Failed to fetch nearby animals"
    val exceptionHandler =
        viewModelScope.createExceptionHandler(errorMessage)
        { onFailure(it) } // 3
    viewModelScope.launch(exceptionHandler) { // 4
        // request more animals!
    }
}
```

1. The if condition checks if the state already has animals. Fragment will send the 
   RequestInitialAnimalsList event every time it’s created. Without this 
   condition, you’d make a request every time the configuration changes. This way, 
   you avoid making unnecessary API requests. If there are no animals, though, you 
   call loadNextAnimalPage().

2. Yes, those double bangs are on purpose. Don’t be afraid of using them when you 
   want to make sure that nullable values exist. The sooner your app crashes, the 
   sooner you can fix the problem. Of course, don‘t use them without weighing the 
   consequences. If, for some reason, you can’t use tests or don’t have a QA team 
   testing the app, be more careful

3. You create a CoroutineExceptionHandler through a custom 
   createExceptionHandler extension function on viewModelScope. It takes in a 
   lambda, which in turn takes a Throwable. You call onFailure() in the lambda, 
   then pass it that same Throwable.

4. You launch a coroutine on viewModelScope, passing in the 
   CoroutineExceptionHandler to the launch extension function.

CoroutineExceptionHandler is a global solution for exception handling that will 
catch exceptions even from child coroutines. It only works if you set it on the parent
coroutine. It’ll ignore exceptions if you set it on a child coroutine.

A `CoroutineExceptionHandler` can be called from any thread. So, if `action` is supposed to  run in the main thread, you need to be careful and call this function on the a scope that  runs in the main thread, such as a `viewModelScope`.

### Handling errors

```kotlin
private fun onFailure(failure: Throwable) {
    when (failure) {
        is NetworkException,
        is NetworkUnavailableException -> {
            _state.value = state.value!!.copy(
                loading = false,
                failure = Event(failure)
            )
        }
        is NoMoreAnimalsException -> {
            _state.value = state.value!!.copy(
                noMoreAnimalsNearby = true,
                failure = Event(failure)
            )
        }
    }
}

```

1. For now, you’re only handling NetworkException and 
   NetworkUnavailableException. The former is a new exception that avoids 
   having Retrofit code in the presentation layer. Check requestMoreAnimals in 
   PetFinderAnimalRepository and you’ll see that it throws a NetworkException
   — a domain exception — when Retrofit’s HttpException occurs.

2. Notice how you’re updating the state. You’re not mutating the object, but rather 
   replacing it with an updated copy of itself. Data classes implement this copy
   method, which really comes in handy her

3. Again, you use Event to wrap Throwable so the UI reacts to it only once.
