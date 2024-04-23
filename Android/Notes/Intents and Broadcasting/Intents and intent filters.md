# Intents and intent filters

An `intent` is a messaging object which requests an action from another `AppComponent`. An intent facilitates communications between two component in several ways:

- **Starting an activity:**  You can start a new activity by passing an intent to `startActivity()`. The intent describes the activity to start and carries the necessary data.
  
  If you want to recieve a result from the activity when it finishes, call `startActivityForResult`. Your activity recieve a new intent inside `onActivityResult` callback.

- **Start a service:** A service is a component that performs background tasks without a user interface. For example, you can start a service using `JobScheduler`.

- **Delivering a broadcast:** A broadcast is a message that any app can recieve. The system delivers various broadcasts for system events, such as when the system boots up or when the device start charging. You can deliver brodcast to other apps by sending an intent to `sendBroadcast()`

There are two types of intents:

- **Explicit Intents:** These types of intents are used when you exactly know what component or class you want to start. You specify the exact component you want to start by its name.
  
  ```kotlin
  val intent = Intent(this, TargetActivity::class.java)
  startActivity(intent)
  ```

- **Implicit Intents:** These types of intents does not specify any name. Instead, it declares a general action to perfom, which allows a component from another app handle the action. Android will find the best component to perfom the action based on the intent filters specified in the manifest file. In the case that multiple components are compatible for specific intent, the android system displays a dialog so the user can pick which app to use.
  
  ```kotlin
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
  startActivity(intent)
  ```

**What is an intent filter?**

Intent filter is an expression in Manifest file which specifies the type of intents that the compoent would like to recieve.

- **Action:** The element specifies the action that the intent must have in order to match the filter. The value is the string name of the action, such as `android.intent.action.VIEW or android.intent.action.MAIN`.

- **Category:** The <category> element specifies additional categories that the intent must have in order to match the filter. Common categories include android.intent.category.LAUNCHER (for the main entry point of the app) and android.intent.category.DEFAULT (for implicit intents).

- **Data:** The <data> element specifies the type of data that the intent must have in order to match the filter. This can include the URI scheme (like http or file), the host name, the port, the path, and the MIME type.

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:host="localhost"
        android:scheme="http" />
</intent-filter>
```
