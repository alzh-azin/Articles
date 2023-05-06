# Guide to app architecture

A typical Android app contains multiple [app components](https://developer.android.com/guide/components/fundamentals#components), including [activities](https://developer.android.com/guide/components/activities/intro-activities), [fragments](https://developer.android.com/guide/fragments), [services](https://developer.android.com/guide/components/services), [content providers](https://developer.android.com/guide/topics/providers/content-providers), and [broadcast receivers](https://developer.android.com/guide/components/broadcasts).

## Common architectural principles

As Android apps grow in size, it's important to define an architecture that allows the app to scale, increases the app's robustness, and makes the app easier to test.

An app architecture defines the boundaries between parts of the app and the responsibilities each part should have. In order to meet the needs mentioned above, you should design your app architecture to follow a few specific principles.

### Separation of concerns

It's a common mistake to write all your code in an [`Activity`](https://developer.android.com/reference/android/app/Activity) or a [`Fragment`](https://developer.android.com/reference/android/app/Fragment). These UI-based classes should only contain logic that handles UI and operating system interactions. By keeping these classes as lean as possible, you can avoid many problems related to the component lifecycle, and improve the testability of these classes.

Keep in mind that you don't own implementations of `Activity` and `Fragment`; rather, these are just glue classes that represent the contract between the Android OS and your app. The OS can destroy them at any time based on user interactions or because of system conditions like low memory. To provide a satisfactory user experience and a more manageable app maintenance experience, it's best to minimize your dependency on them.
