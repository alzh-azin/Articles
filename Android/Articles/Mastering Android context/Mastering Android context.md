# Mastering Android context

Have you ever encountered this question: What is difference between `getContext()`, `this`, `getBaseContext()`, and `getApplicationContext()`? If yes, this article will help clarify most of your confusion.



Let’s face it, Context is one of the most poorly designed features of the Android API. You could call it the “God” object.



An Android app or application package kit (APK) is a bundle of components. These components are defined in the Manifest, and consist mainly of Activity (UI), Service (Background), BroadcastReceiver (Action), ContentProvider (Data), and Resources (images, strings etc).



The developer can choose to expose those components to a system using an intent-filter. For example: send email or share picture. They can also choose to expose the components only to other components of their app.

Similarly, the Android operating system was also designed to expose components. A few well known are WifiManager, Vibrator, and PackageManager.

> Context is the bridge between components. You use it to communicate between components, instantiate components, and access components.

### **Your own components**

We use context to instantiate our components with Activity, Content Provider, BroadcastReceiver, and so on. We use it to access resources and filesystems as well.



### **Your component and a system component**

Context acts as an entry point to the Android system. Some well-used System components are WifiManager, Vibrator, and PackageManager. You can access WifiManager using `context.getSystemService(Context.WIFI_SERVICE)`.

In this same way, you can use context to access the filesystem dedicated to your app as a user in OS.



### **Your own component and some other app’s component**
