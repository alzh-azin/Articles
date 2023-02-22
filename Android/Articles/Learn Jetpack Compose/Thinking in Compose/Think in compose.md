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

### Composable functions can run in parallel

Compose can optimize recomposition by running composable functions in parallel. This lets Compose take advantage of multiple cores, and run composable functions not on the screen at a lower priority.

This optimization means a composable function might execute within a pool of background threads. If a composable function calls a function on a `ViewModel`, Compose might call that function from several threads at the same time.

To ensure your application behaves correctly, all composable functions should have no side-effects. Instead, trigger side-effects from callbacks such as `onClick` that always execute on the UI thread.

```kotlin
@Composable
fun ListComposable(myList: List<String>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            for (item in myList) {
                Text("Item: $item")
            }
        }
        Text("Count: ${myList.size}")
    }
}
```

This code is side-effect free, and transforms the input list to UI. This is great code for displaying a small list. However, if the function writes to a local variable, this code will not be thread-safe or correct:

```kotlin
@Composable
@Deprecated("Example with bug")
fun ListWithBug(myList: List<String>) {
    var items = 0

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            for (item in myList) {
                Text("Item: $item")
                items++ // Avoid! Side-effect of the column recomposing.
            }
        }
        Text("Count: $items")
    }
}
```

In this example, `items` is modified with every recomposition. That could be every frame of an animation, or when the list updates. Either way, the UI will display the wrong count. Because of this, writes like this are not supported in Compose; by prohibiting those writes, we allow the framework to change threads to execute composable lambdas.

### Recomposition skips as much as possible

When portions of your UI are invalid, Compose does its best to recompose just the portions that need to be updated. This means it may skip to re-run a single Button's composable without executing any of the composables above or below it in the UI tree.

```kotlin
/**
 * Display a list of names the user can click with a header
 */
@Composable
fun NamePicker(
    header: String,
    names: List<String>,
    onNameClicked: (String) -> Unit
) {
    Column {
        // this will recompose when [header] changes, but not when [names] changes
        Text(header, style = MaterialTheme.typography.h5)
        Divider()

        // LazyColumn is the Compose version of a RecyclerView.
        // The lambda passed to items() is similar to a RecyclerView.ViewHolder.
        LazyColumn {
            items(names) { name ->
                // When an item's [name] updates, the adapter for that item
                // will recompose. This will not recompose when [header] changes
                NamePickerItem(name, onNameClicked)
            }
        }
    }
}

/**
 * Display a single name the user can click.
 */
@Composable
private fun NamePickerItem(name: String, onClicked: (String) -> Unit) {
    Text(name, Modifier.clickable(onClick = { onClicked(name) }))
}
```

Each of these scopes might be the only thing to execute during a recomposition. Compose might skip to the `Column` lambda without executing any of its parents when the `header` changes. And when executing `Column`, Compose might choose to skip the `LazyColumn`'s items if `names` didn't change.

Again, executing all composable functions or lambdas should be side-effect free. When you need to perform a side-effect, trigger it from a callback.

### Recomposition is optimistic

Recomposition starts whenever Compose thinks that the parameters of a composable might have changed. Recomposition is *optimistic,* which means Compose expects to finish recomposition before the parameters change again. If a parameter *does* change before recomposition finishes, Compose might cancel the recomposition and restart it with the new parameter.

When recomposition is canceled, Compose discards the UI tree from the recomposition. If you have any side-effects that depend on the UI being displayed, the side-effect will be applied even if composition is canceled. This can lead to inconsistent app state.

Ensure that all composable functions and lambdas are idempotent and side-effect free to handle optimistic recomposition.

### Composable functions might run quite frequently

In some cases, a composable function might run for every frame of a UI animation. If the function performs expensive operations, like reading from device storage, the function can cause UI jank.

For example, if your widget tried to read device settings, it could potentially read those settings hundreds of times a second, with disastrous effects on your app's performance.

If your composable function needs data, it should define parameters for the data. You can then move expensive work to another thread, outside of composition, and pass the data to Compose using `mutableStateOf` or `LiveData`.
