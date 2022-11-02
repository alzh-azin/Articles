# Thinking in Compose

Over the last several years, the entire industry has started shifting to a declarative UI model, which greatly simplifies the engineering associated with building and updating user interfaces. The technique works by conceptually regenerating the entire screen from scratch, then applying only the necessary changes. This approach avoids the complexity of manually updating a stateful view hierarchy. Compose is a declarative UI framework.

One challenge with regenerating the entire screen is that it is potentially expensive, in terms of time, computing power, and battery usage. To mitigate this cost, Compose intelligently chooses which parts of the UI need to be redrawn at any given time. This does have some implications for how you design your UI components, as discussed in [Recomposition](https://developer.android.com/jetpack/compose/mental-model?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fjetpack-compose-for-android-developers-1%23article-https%3A%2F%2Fdeveloper.android.com%2Fjetpack%2Fcompose%2Fmental-model#recomposition).

## The declarative paradigm shift

With many imperative object-oriented UI toolkits, you initialize the UI by instantiating a tree of widgets. You often do this by inflating an XML layout file. Each widget maintains its own internal state, and exposes getter and setter methods that allow the app logic to interact with the widget.

In Compose's declarative approach, widgets are relatively stateless and do not expose setter or getter functions. In fact, widgets are not exposed as objects. You update the UI by calling the same composable function with different arguments.

## Recomposition

Recomposition is the process of calling your composable functions again when inputs change. This happens when the function's inputs change. When Compose recomposes based on new inputs, it only calls the functions or lambdas that might have changed, and skips the rest. By skipping all functions or lambdas that don't have changed parameters, Compose can recompose efficiently.

Never depend on side-effects from executing composable functions, since a function's recomposition may be skipped. If you do, users may experience strange and unpredictable behavior in your app. A side-effect is any change that is visible to the rest of your app. For example, these actions are all dangerous side-effects:

- Writing to a property of a shared object
- Updating an observable in `ViewModel`
- Updating shared preferences

 If you need to do expensive operations, such as reading from shared preferences, do it in a background coroutine and pass the value result to the composable function as a parameter.

As an example, this code creates a composable to update a value in `SharedPreferences`. The composable shouldn't read or write from shared preferences itself. Instead, this code moves the read and write to a `ViewModel` in a background coroutine. The app logic passes the current value with a callback to trigger an update.

```kotlin
@Composable
fun SharedPrefsToggle(
    text: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row {
        Text(text)
        Checkbox(checked = value, onCheckedChange = onValueChanged)
    }
}
```

### Composable functions can execute in any order

If you look at the code for a composable function, you might assume that the code is run in the order it appears. But this isn't necessarily true. If a composable function contains calls to other composable functions, those functions might run in any order. Compose has the option of recognizing that some UI elements are higher priority than others, and drawing them first.

For example, suppose you have code like this to draw three screens in a tab layout:

```kotlin
@Composable
fun ButtonRow() {
    MyFancyNavigation {
        StartScreen()
        MiddleScreen()
        EndScreen()
    }
}
```

The calls to `StartScreen`, `MiddleScreen`, and `EndScreen` might happen in any order. This means you can't, for example, have `StartScreen()` set some global variable (a side-effect) and have `MiddleScreen()` take advantage of that change. Instead, each of those functions needs to be self-contained.
