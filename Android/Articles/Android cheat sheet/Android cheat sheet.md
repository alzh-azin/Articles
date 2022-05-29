- Refresh dependency: :app:openProdDebug --refresh-dependencies
- disable theme overlay for card view

```kotlin
 android:theme="@style/ThemeOverlay.Signal.DisableElevationOverlay"


<style name="ThemeOverlay.Signal.DisableElevationOverlay" parent="">
     <item name="elevationOverlayEnabled">false</item>
</style>


```
