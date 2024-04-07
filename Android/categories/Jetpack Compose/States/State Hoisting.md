# State Hoisting

In Composable functions, state that is read or modified by multiple functions should live in a common ancestor—this process is called **state hoisting**. To *hoist* means to *lift* or *elevate*.

Making state hoistable avoids duplicating state and introducing bugs, helps reuse composables, and makes composables substantially easier to test.

The **source of truth** belongs to whoever creates and controls that state.

We also need to share state with the child composable but we are not going to pass it directly.Instead of letting child composable mutate our state, it would be better to let it notify us when the user clicked on the *Continue* button.

How do we pass events up? By **passing callbacks down**. Callbacks are functions that are passed as arguments to other functions and get executed when the event occurs.

```kotlin
@Composable
fun MyApp(modifier: Modifier = Modifier) {

    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            Greetings()
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }

}
```

By passing a function and not a state to `OnboardingScreen` we are making this composable more reusable and protecting the state from being mutated by other composables. In general, it keeps things simple.

Reference:

https://developer.android.com/
