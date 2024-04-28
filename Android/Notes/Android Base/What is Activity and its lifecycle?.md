# What is Activity and its lifecycle?

Activity is one of the Android components which represent a single screen or user interface that provides a window for user interaction. The Activity is a subclass of the `ContextThemeWrapper` class, which it self is a subclass of the `Context` class.

The Activity class has a lifecycle, which refers to the various states an activity can go through from the time it is created until it is destroyed. Undrestanding the activity lifecycle is important for managing resources, handling user events an better user experience.

`onCreate()`: This is the first callback that is called when the Activity is being created.

In this callback, you typically initialize essential coponents of the Activity such as setting up the user interface layout (calling `setContent` function), initialize variables, and binding data.

`onStart()`: This method is called `after the Activity becomes visible to the user`. It is a good place to start any non-ui related operations like connecting to the server or a sensor.

`onResume()`: This method is called when the activity is ready to start interacting with the user. At this point, the Activity is in the foreground and is the top-most visible Activity.

`onPause()`: This method is called when the Activity is going into the background but has not yet been killed. It's a good place to persist data or stop any CPU-intensive tasks that may be running.

`onStop()`: This method is called when the Activity is no longer visible to the user. It's typically used to release resources that are not needed while the Activity is not visible.

`onRestart()`: This method is called after the onStop() method when the Activity is about to restart and become visible again. After this method, onStart() is going to be called.

`onDestroy()`: This method is called before the Activity is destroyed. It's used to release any remaining resources associated with the Activity.


