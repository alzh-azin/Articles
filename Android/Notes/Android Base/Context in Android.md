# Context in Android

- it has the current state of the application

- It can be used to get information about Activity and Application

- It can be used to get access to resources, database, preferences and etc.

- Both Activity and Application classes extends context class

- Activity directly extend ContextThemeWrapper because it needs information about theme of the application but Application class directly extends context class.

> Wrong use of context can leads to momory leak in your application

**There are two types of context**

- Applicaion context

- Activity context

### Application Context

It is a singleton istance of context which is tied to the lifecycle of the application. It is used when you need a context that its lifecycle is beyond the lifecycle of the activity and it is separetad from current context.

> If you want to create a singleton object which needs a context, always pass the application context. Otherwise, if you pass the activity context, that context might be no longer available due to activity is destroyed but your object keeps a reference of that context and it doesnt to get garbage collected and it leads to memory leak.

### Activity Context

This type of context in accessible in the activity. Its lifecycle is tied to the lifecycle of the activity. It is used when you need a context in the scope of the activity or you want to create an object which its lifecycle is tied to the lifecycle of the activity.

### When to use which context?

- In the singleton classes like database -> use application context

- When you are in the activity and you want to use it for getting resources, showing toast and dialogs, you should use acitivty context

> Generaly, use the nearest context which is available to you. the nearest context is Activity context. When you are in Application, the nearest context is the Application context. If Singleton, use the Application Context.
