# launchMode in Android

We use launchMode to give instructions to android operating system about how to launch the activity.

### SingleTask

- In launchMode, the system ensures that only one instance of the activity exists in the entire stack.

- If another instance of the activity is already exists in the stack, the system destroys another activites that are on top of that activity and bring out that activity to the foreground.

- When the activity is restored, it receive new intent through the `onNewIntent()` method.

### SingleInstance

- Similar to singleTask but more stricker. The system ensures only one instance of an activity exists in the entire system, not only in a one stack.

- It also receive new intent when it is restored, but previous activities are not destroyed.

### Standard

- Every time, a new instance of the activity is created and it is placed on top of the stack

### SingleTop

- If an instance of the activity already exist at the top of the current stack, the existing instance is reused.

- When the activity is restored, it receive new intent through the `onNewIntent()` method.
