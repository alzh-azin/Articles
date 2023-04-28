# Material Design 3

#### Color usage

- Primary is the base color, used for main components like prominent buttons, active states, and the tint of elevated surfaces.
- The secondary key color is used for less prominent components in the UI, such as filter chips, and expands the opportunity for color expression.
- The tertiary key color is used to derive the roles of contrasting accents that can be used to balance primary and secondary colors or bring enhanced attention to an element.

## Elevation

Material 3 represents elevation mainly using tonal color overlays. This is a new way to differentiate containers and surfaces from each other — increasing tonal elevation uses a more prominent tone — in addition to shadows.



![m3-elevation.png]([C:\Users\Azin\Desktop\Material%203\m3-elevation.png](https://raw.githubusercontent.com/alzh-azin/Learning/master/Android/categories/Material%20You/Material%20Design%203/1.PNG))

Elevation overlays in dark themes have also changed to tonal color overlays in Material 3. The overlay color comes from the primary color slot.

![m3-surface.png]([C:\Users\Azin\Desktop\Material%203\m3-surface.png](https://raw.githubusercontent.com/alzh-azin/Learning/master/Android/categories/Material%20You/Material%20Design%203/2.PNG))

The M3 [Surface](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#Surface(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.foundation.BorderStroke,androidx.compose.ui.unit.Dp,kotlin.Function0)) — the backing composable behind most M3 components — includes support for both tonal and shadow elevation:

```kotlin
Surface(
    modifier = modifier,
    tonalElevation = {.. } 
    shadowElevation = {.. }
) {
    Column(content = content)
}
```


