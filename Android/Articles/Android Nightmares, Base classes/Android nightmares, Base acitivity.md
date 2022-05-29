## # Android Nightmares | Base classes

These classes are the wrong consequence of a correct principle in software development: **Don’t Repeat Yourself**

> **“Don’t repeat yourself**” (**DRY**) is a principle of software development aimed at reducing repetition of software patterns,replacing it with abstractions or using data normalization to avoid redundancy.

> D**elegation pattern** is an object-oriented design pattern that allows object composition to achieve the same code reuse as inheritance

You can implement an interface and delegate the actual code to another class. Also, thanks to Lifecycle components, we can bind our logic to lifecycle.

You only need

- Interface & Implementation for each feature (Single Responsibility principle 💓)

- Your Activity/Fragment that implements the interface, delegates to Impl and attaches the lifecycle.

Final result:

```kotlin
class MyActivity:
    AppCompatActivity(),
    AnalyticsDelegate by AnalyticsDelegateImpl(),
    LogoutDelegate by LogoutDelegateImpl()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerAnalytics(lifecycle)
        registerLogout(this)
    }
}
```

Your delegates will look like these:

```kotlin
interface AnalyticsDelegate {
    fun registerAnalytics(lifecycle: Lifecycle)
}

class AnalyticsDelegateImpl: AnalyticsDelegate, DefaultLifecycleObserver {
    override fun registerAnalytics(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        traceEvent("Activity started")
    }

    override fun onStop(owner: LifecycleOwner) {
        traceEvent("Activity stopped")
    }

    private fun traceEvent(event: String) {
        //MyAnalytics.newEvent(event)
    }
}
```

```kotlin
interface LogoutDelegate {
    fun registerLogout(activity: AppCompatActivity)
}

class LogoutDelegateImpl: LogoutDelegate, DefaultLifecycleObserver {
    private lateinit var activity: AppCompatActivity

    override fun registerLogout(activity: AppCompatActivity) {
        this.activity = activity
        this.activity.lifecycle.addObserver(this)
    }

    private lateinit var logoutReceiver: BroadcastReceiver
    override fun onCreate(owner: LifecycleOwner) {
        logoutReceiver = LogoutReceiver()
        activity.registerReceiver(logoutReceiver, IntentFilter("ACTION_LOGOUT"))
        //unregister missing for keeping the sample code smaller!
    }

    inner class LogoutReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            activity.finish()
        }
    }
}
```
