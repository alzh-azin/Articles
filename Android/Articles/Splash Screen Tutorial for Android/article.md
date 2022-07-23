# Splash Screen Tutorial for Android

### Choosing the Target SDK Version

Android 12 introduced the *SplashScreen API*. The `targetSdkVersion` must be set to at least `31` in *build.gradle*.

Luckily, the *AndroidX SplashScreen compat library* supports backward compatibility.

### Default Android 12 Splash Behavior

As the app starts, you’ll see the default splash screen: the app logo centered on a screen colored to match `windowBackground` from your app theme.

If you don’t have any legacy code and you don’t need further customization, you don’t need to do anything else. Users will see the default splash from Android 12 onward automatically. However, even without additional customization, implementing the *SplashScreen compat library* can help improve the experience for users on older versions. Read on to find out why splash screens are important.

### App Startup States

- ***Cold start***: Any time your app is freshly launched, including the first launch after installation or device restart, it’s considered a cold start. The system also kills apps in the background to free memory. If your app hasn’t been used for a while or the device is low on memory, then even if your app has previously been open, the system might still treat it as a cold start. Launching from that state takes the longest because the system has to create the app process and load in the app. This is a perfect use case for showing a splash screen that gives the impression of a shorter delay while giving you the opportunity to get creative with your branding.

- ***Warm start***: Your app can be in various states of retainment as the system starts to release memory. That creates a warm start: The system does not need to initialize everything but has lost some information and needs to reload it. For example, the app process might still be running, but the activity requires re-creation to display. That doesn’t take as long as a cold start, but it’s often long enough to warrant a splash screen.

- ***Hot start***: If the system has the app process and layout information in memory, it doesn’t need to re-initialize anything and can bring the app back to the foreground. This is a hot start and it’s quick, leaving no time for a splash screen.

### Splash Screens Over the Years

Splash screens were once viewed as an anti-pattern. They were often misused as a heavy-handed approach to embedding branding into apps, intentionally delaying users from reaching the content for a few seconds. But as Android evolved and app requirements grew, the time taken to launch apps began to increase. The Material Design guidelines started recommending a launch screen instead of the blank loading screen. Developers had a couple of ways to add the splash, but neither requirements nor libraries existed to aid with consistency across codebases.

Fast forward to Android 12, which added the *SplashScreen API*. From Android 12 onward, all apps show a default splash screen, and developers can customize it according to their requirements.

### Customizable Elements

Splash screens have an icon and background you can customize. Android 12 takes those from the app icon. Even if you’re happy with the default behavior, defining it explicitly means it can be backported to older versions.

### Adding the Adaptive Icon

```kotlin
  <style name="Theme.App.Starting" parent="Theme.SplashScreen">
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
    <item name="postSplashScreenTheme">@style/AppTheme</item>
    <item name="windowSplashScreenBackground">@color/ic_launcher_background</item>
  </style>
```

### Setting the Style

Now you have created the style for your splash screen, you need to tell your app to use it. Open *AndroidManifest.xml* to where *MainActivity* is declared. Inside the declaration, add:

```kotlin
android:theme="@style/Theme.App.Starting"
```

### Installing the Splash Screen

In `onCreate()`

```kotlin
val splashScreen = installSplashScreen()
```

It handles the splash screen transition and returns an instance of the splash screen.  It’s important you call this function before calling `super.onCreate(savedInstanceState)`

## Adding More Customization

### Adding an Icon Background

To enable the icon background, your splash theme needs a different parent. Open *styles.xml* where you declared your splash theme. Replace the parent with:

`Theme.SplashScreen.IconBackground`

Add the background color as an item in the style:

```kotlin
<item name="windowSplashScreenIconBackgroundColor">@color/yellow6</item>
```

### Creating a New Style for Android 12

Android 12 or higher is required for some features of the *SplashScreen API*. Create a new resource directory with the API 31 qualifier, which tells the system those values apply only when the device runs a valid version of Android.

```kotlin
<!-- Splash Screen Theme (Android 12+) -->
<style name="Theme.App.Starting" parent="Theme.SplashScreen.IconBackground">
 <item name="windowSplashScreenBackground">@color/ic_launcher_background</item>
 <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
 <item name="windowSplashScreenIconBackgroundColor">@color/yellow6</item>
 <item name="postSplashScreenTheme">@style/AppTheme</item>
</style>
```

Two styles called `Theme.App.Starting` are declared in your codebase. One in *values/styles.xml*, and the other in *values-v31/styles.xml*. You have previously set this theme for the *MainActivity* in the *AndroidManifest.xml*. When the app is launched, the system will first check which version of Android the device is running, and take the style from *values-v31/styles.xml* if it applies, before falling back to *values/styles.xml* if it does not. This is why it’s important that both files use the same name to declare the splash screen theme.

### Adding an Animated Icon

```kotlin
<item name="windowSplashScreenAnimatedIcon">@drawable/anim_brush</item>
<item name="windowSplashScreenAnimationDuration">1000</item>
```

Use [`windowSplashScreenAnimationDuration`](https://developer.android.com/reference/android/R.attr#windowSplashScreenAnimationDuration) to indicate the duration of the splash screen icon animation. Setting this won't have any effect on the actual time during which the splash screen is shown, but you can retrieve it when customizing the splash screen exit animation using [`SplashScreenView#getIconAnimationDuration`](https://developer.android.com/guide/topics/ui/splash-screen/reference/android/window/SplashScreenView#getIconAnimationDuration()).

### Adding a Branding Image

```kotlin
<item name="android:windowSplashScreenBrandingImage">@drawable/splash_branding_image</item>
```

### Extending the Duration

Though discouraged, you’ll occasionally want to hold the user on the splash screen before letting them into the app. For example, if you are loading settings or a small amount of data from an initial setup network call, you might need that in place before the user enters the app. To do that, you can delay the app from drawing its first frame, thus delaying the splash screen dismissal.

```kotlin
val content: View = findViewById(android.R.id.content)
content.viewTreeObserver.addOnPreDrawListener(
   object : ViewTreeObserver.OnPreDrawListener {
     override fun onPreDraw(): Boolean {
       return if (contentHasLoaded) {
         content.viewTreeObserver.removeOnPreDrawListener(this)
         true
       } else false
     }
   }
)
```

### Changing the Exit Animation

You can’t change the launch animation, but you can get into the exit animation through the *SplashScreen API* and customize it.

```kotlin
splashScreen.setOnExitAnimationListener {
    splashScreenView ->

    val slideBack = ObjectAnimator.ofFloat(
        splashScreenView.view,
        View.TRANSLATION_X,
        0f,
        -splashScreenView.view.width.toFloat()
    ).apply {
        interpolator = DecelerateInterpolator()
        duration = 800L
        doOnEnd {
            Log.d("TestSplash", "setupSplashScreen: ")
            splashScreenView.remove()
        }
    }

    slideBack.start()
}
```

If you use an animated icon and need it to finish animating before dismissing the splash screen, ensure the exit animation isn’t triggered too early. Within the `OnExitAnimationListener` you can access the `splashScreenView.iconAnimationDuration` and`splashScreenView.iconAnimationStart` values and use them to determine whether enough time has passed since the icon animation has started to allow it to finish. If you don’t check, the splash screen will dismiss when your app is ready, and that might occur before your animation has finished.

## Migrating Your Project


