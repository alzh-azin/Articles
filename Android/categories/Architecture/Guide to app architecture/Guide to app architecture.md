# Guide to app architecture

A typical Android app contains multiple [app components](https://developer.android.com/guide/components/fundamentals#components), including [activities](https://developer.android.com/guide/components/activities/intro-activities), [fragments](https://developer.android.com/guide/fragments), [services](https://developer.android.com/guide/components/services), [content providers](https://developer.android.com/guide/topics/providers/content-providers), and [broadcast receivers](https://developer.android.com/guide/components/broadcasts).

## Common architectural principles

As Android apps grow in size, it's important to define an architecture that allows the app to scale, increases the app's robustness, and makes the app easier to test.

An app architecture defines the boundaries between parts of the app and the responsibilities each part should have. In order to meet the needs mentioned above, you should design your app architecture to follow a few specific principles.

### Separation of concerns

It's a common mistake to write all your code in an [`Activity`](https://developer.android.com/reference/android/app/Activity) or a [`Fragment`](https://developer.android.com/reference/android/app/Fragment). These UI-based classes should only contain logic that handles UI and operating system interactions. By keeping these classes as lean as possible, you can avoid many problems related to the component lifecycle, and improve the testability of these classes.

Keep in mind that you don't own implementations of `Activity` and `Fragment`; rather, these are just glue classes that represent the contract between the Android OS and your app. The OS can destroy them at any time based on user interactions or because of system conditions like low memory. To provide a satisfactory user experience and a more manageable app maintenance experience, it's best to minimize your dependency on them.

### Drive UI from data models

Another important principle is that you should drive your UI from data models, preferably persistent models. Data models represent the data of an app. They're independent from the UI elements and other components in your app. This means that they are not tied to the UI and app component lifecycle, but will still be destroyed when the OS decides to remove the app's process from memory.

Persistent models are ideal for the following reasons:

- Your users don't lose data if the Android OS destroys your app to free up resources.

- Your app continues to work in cases when a network connection is flaky or not available.

If you base your app architecture on data model classes, you make your app more testable and robust.

### Single source of truth

When a new data type is defined in your app, you should assign a Single Source of Truth (SSOT) to it. The SSOT is the *owner* of that data, and only the SSOT can modify or mutate it. To achieve this, the SSOT exposes the data using an immutable type, and to modify the data, the SSOT exposes functions or receive events that other types can call.

This pattern brings multiple benefits:

- It centralizes all the changes to a particular type of data in one place.
- It protects the data so that other types cannot tamper with it.
- It makes changes to the data more traceable. Thus, bugs are easier to spot.

### Unidirectional Data Flow

The [single source of truth principle](https://developer.android.com/topic/architecture#single-source-of-truth) is often used in our guides with the Unidirectional Data Flow (UDF) pattern. In UDF, **state** flows in only one direction. The **events** that modify the data flow in the opposite direction.

In Android, state or data usually flow from the higher-scoped types of the hierarchy to the lower-scoped ones. Events are usually triggered from the lower-scoped types until they reach the SSOT for the corresponding data type. For example, application data usually flows from data sources to the UI. User events such as button presses flow from the UI to the SSOT where the application data is modified and exposed in an immutable type.

This pattern better guarantees data consistency, is less prone to errors, is easier to debug and brings all the benefits of the SSOT pattern.

### Modern App Architecture

### UI layer

The UI layer is made up of two things:

- UI elements that render the data on the screen. You build these elements using Views or [Jetpack Compose](https://developer.android.com/jetpack/compose) functions.
- State holders (such as [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) classes) that hold data, expose it to the UI, and handle logic.

![](C:\Users\Azin\AppData\Roaming\marktext\images\2023-05-06-20-53-16-image.png)

### Data layer

The data layer of an app contains the *business logic*. The business logic is what gives value to your app—it's made of rules that determine how your app creates, stores, and changes data.

Repository classes are responsible for the following tasks:

- Exposing data to the rest of the app.
- Centralizing changes to the data.
- Resolving conflicts between multiple data sources.
- Abstracting sources of data from the rest of the app.
- Containing business logic.

### Domain layer

The domain layer is an optional layer that sits between the UI and data layers.

The domain layer is responsible for encapsulating complex business logic, or simple business logic that is reused by multiple ViewModels. This layer is optional because not all apps will have these requirements. You should use it only when needed—for example, to handle complexity or favor reusability.

Classes in this layer are commonly called *use cases* or *interactors*. Each use case should have responsibility over a *single* functionality.

## Manage dependencies between components

- [Dependency injection (DI)](https://developer.android.com/training/dependency-injection): Dependency injection allows classes to define their dependencies without constructing them. At runtime, another class is responsible for providing these dependencies.

## General best practices

**Don't store data in app components.**

Avoid designating your app's entry points—such as activities, services, and broadcast receivers—as sources of data. Instead, they should only coordinate with other components to retrieve the subset of data that is relevant to that entry point. Each app component is rather short-lived, depending on the user's interaction with their device and the overall current health of the system.

**Reduce dependencies on Android classes.**

Your app components should be the only classes that rely on Android framework SDK APIs such as [`Context`](https://developer.android.com/reference/android/content/Context), or [`Toast`](https://developer.android.com/guide/topics/ui/notifiers/toasts). Abstracting other classes in your app away from them helps with testability and reduces [coupling](https://en.wikipedia.org/wiki/Coupling_(computer_programming)) within your app.
