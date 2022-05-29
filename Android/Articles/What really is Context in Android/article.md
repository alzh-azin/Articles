# What really is Context in Android?

### What is Context?

It’s an abstract class that provides an interface to access application resources and classes. For example, it’s what allows you to access any resource under the */res*directory and what allows you to dynamically initialize *View* objects from an Activity or Fragment.

Context also provides methods to perform application level operations. For example, you need a Context to start Activities and receive and broadcast intents.

### How is it created?

Context is an abstract class, so it needs to have a subclass that implements its methods to be useful. The Android system conveniently implements this in a class called ContextImpl.

As shown below, another class called ContextWrapper simply uses the methods implemented by ContextImpl and delegates their call to its own subclasses. Now, to the part that really matters, how do Application, Activities and Services get their context?



![context.PNG](C:\Users\azin.alizadeh\Desktop\Learning\Android\Articles\What%20really%20is%20Context%20in%20Android\resources\context.PNG)

### Where do you come from, Context?

Application and Service classes are direct subclasses of the ContextWrapper classes, and thus have access to the Context implementation directly.

On the other hand, the Activity class is a direct subclass of ContextThemeWrapper, which is an extension of ContextWrapper that simply adds the theme defined on the AndroidManifest. This should make sense, since Activities are UI components and need to know about the app’s theme, while Application and Services don’t.

So, in an Android project, we have one Context for the Application class, one Context for each Service component and one Context for each Activity component.

### What can Contexts do for us?

Activity Contexts are used in ways that are familiar to many of us. For example, they are used to start a new Activity via *startActivity(Intent)* and to inflate a layout via *inflate(Int, ViewGroup)*. Other use cases for Application and Service contexts are shown below.



![context usecase.PNG](C:\Users\azin.alizadeh\Desktop\Learning\Android\Articles\What%20really%20is%20Context%20in%20Android\resources\context%20usecase.PNG)

Now, there are two noteworthy limitations with Application and Service contexts.

> You should not inflate a layout from them.

Remember, Application and Service contexts do not inherit from the ContextThemeWrapper class, and thus, do not have access to the application’s theme, so the inflated layout would have the default theme.

> Usually, you should not start an Activity from them.

When an Activity is started from another Activity, they are properly added to the Activity stack. When started from the Application or Service context, the Activity is added to the start of a new task on the history stack.

### Know your Context, avoid memory leaks

Incorrect usage of Contexts could result in stale references to Context objects, which can disrupt the garbage collection process and cause memory leaks.

To illustrate this, consider the example below. In this case, we are saving a reference of the Activity’s Context in a companion object via a *TextView*. The issue is that companion objects are similar to static fields in Java, and are not garbage collected when the Activity is destroyed. This means that the the reference to the Activity’s Context will exist after it’s been destroyed.

```kotlin
class MainActivity: Activity() {
    companion object {
        lateinit var mTextView: TextView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        mTextView = findViewById(R.id.sample) as TextView
    }
    ...
}
```

### Finally, some valuable lessons

> Use Application Context when possible to avoid memory leak issues.

Since the Application exists for as long as the app exists, this will avoid situations like the one above.

> Do not reference Activity Context in objects that may outlive the Activity lifecycle.

This is specially important when dealing with static fields in Java or companion objects in Kotlin, since these are not garbage collected. If you find yourself in a situation you need to do something similar, make sure to instead use weak references to avoid memory leaks.
