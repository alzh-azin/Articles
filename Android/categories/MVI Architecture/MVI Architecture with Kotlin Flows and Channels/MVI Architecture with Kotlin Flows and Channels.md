# MVI Architecture with Kotlin Flows and Channels

MVI consist of three parts. **Model** — **View** — **Intent**

- **Model** represents the state of the UI. for example UI might have different states like Idle, Loading or Loaded.
- **View** basically sets immutable states that comes from ViewModel and update UI.
- **Intent** is not the traditional Android Intent. It represents user’s intent when made an interaction with UI. Just like clicking a button.

Now let’s move to code

```kotlin
interface UiState

interface UiEvent

interface UiEffect
```

- `UiState` is current state of views.
- `UiEvent` is the user actions.
- `UiEffect` is the side effects like error messages which we want to show only once.

```kotlin
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {
  
    // Create Initial State of View
    private val initialState : State by lazy { createInitialState() }
    abstract fun createInitialState() : State

    // Get Current State
    val currentState: State
        get() = uiState.value

    private val _uiState : MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event : MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect : Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()
  
}
```

## What is the difference between StateFlow — SharedFlow — Channel?

> With the shared flow, events are broadcast to an unknown number (zero or more) of subscribers. In the absence of a subscriber, any posted event is immediately dropped. It is a design pattern to use for events that must be processed immediately or not at all.
> 
> With the channel, each event is delivered to a single subscriber. An attempt to post an event without subscribers will suspend as soon as the channel buffer becomes full, waiting for a subscriber to appear. Posted events are never dropped by default.

For handling `UiState` we use `StateFlow.` `StateFlow` is just like `LiveData` but have initial value. So we have always a state. It is also a kind of `SharedFlow.` We always want to receive last view state when UI become visible.

For handling `UiEvent` we use `SharedFlow.` We want to drop event if there is not any subscriber.

Last, for handling `UiEffect` we use `Channels.` Because `Channels` are hot and we do not need to show side effect again when orientation changed or UI become visible again. Simply we want to replicate `SingleLiveEvent` behavior.

We completed `BaseViewModel` implementation so let’s move to `MainContract` which is a contract between `MainActivity` and `MainViewModel` .

```kotlin
class MainContract {

    // Events that user performed
    sealed class Event : UiEvent {
        object OnRandomNumberClicked : Event()
        object OnShowToastClicked : Event()
    }

    // Ui View States
    data class State(
        val randomNumberState: RandomNumberState
    ) : UiState

    // View State that related to Random Number
    sealed class RandomNumberState {
        object Idle : RandomNumberState()
        object Loading : RandomNumberState()
        data class Success(val number : Int) : RandomNumberState()
    }

    // Side effects
    sealed class Effect : UiEffect {

        object ShowToast : Effect()

    }

}
```

Refrence:

[MVI Architecture with Kotlin Flows and Channels](https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d)


