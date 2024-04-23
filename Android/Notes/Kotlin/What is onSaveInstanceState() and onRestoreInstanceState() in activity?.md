# What is onSaveInstanceState() and onRestoreInstanceState() in activity?

- **onSaveInstanceState:** it is used to store the state of the activity before the activity is destroyed.

- **onRestoreInstanceState:** it is used to restore the saved state of the activity when the activity is recreated after destruction. So, `the onRestoreInstanceState()`  receives the bundle that contains the state of the activity. This function get called after `onStart()` function.
