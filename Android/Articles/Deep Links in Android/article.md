# Deep Links in Android

## Understanding Deep Links

A *deep link* is a URL that navigates to a specific destination in your app. When you click a deep link, Android:

- Opens the user’s preferred app that can handle the link, if it’s available.
- If the preferred app isn’t available, it opens the only app that can handle the link.
- If multiple apps can handle the link, it opens a dialog that lets the user select from one of the apps that can open the link.

### Building Blocks of a Deep Link

Consider the link *https://www.raywenderlich.com/test/code=abcd*. It has the following parts:

- *https*: Identifies the protocol used to access the resource on the internet.
- *www.raywenderlich.com*: The host, which is the name or address of the web server being accessed.
- */test*: The path which specifies a particular page of content.
- *code*: The query parameter you can extract from intents in your destination. *abcd* is the value of the parameter.

## Understanding Intent Filters

To create a deep link to your app’s content you first have to create an *intent filter*. An intent filter specifies the types of intents the activity would like to receive. You define it in the manifest file.

An intent filter has the following elements:

- *action*: For Google Search to reach your intent, you have to specify an `ACTION_VIEW` action.

- *data*: Used to define URI format that resolves to the activity. You can have one or more in a single intent filter. The data tags have a *scheme*, *host*, *path* and *query* parameters to identify the deep link.

- *category*: You have to define some categories for your deep link to work. First, you specify the `BROWSABLE` category so your deep link can work in a browser. Without it, clicking the deep link on a browser won’t resolve to your app.
  
  Second, the `DEFAULT` category lets your app handle implicit intents. If it’s missing, the intent must specify the app component name for the activity to start.

## Creating a Deep Link

```xml
<intent-filter android:label="@string/text_deep_link_title">
  <action android:name="android.intent.action.VIEW" />
  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />
  <data
    android:scheme="https"
    android:host="www.raywenderlich.com"
    android:pathPrefix="/test" />
</intent-filter>
```

## Testing Your Deep Links

```shell
adb shell am start -W -a android.intent.action.VIEW -d 
"https://www.raywenderlich.com/test?code=abcde"
```

## Getting Data From Incoming Intents

```kotlin
private fun handleIntent(intent: Intent?) {
  val appLinkAction: String? = intent?.action
  val appLinkData: Uri? = intent?.data
  showDeepLinkOffer(appLinkAction, appLinkData)
}

```

```kotlin
private fun showDeepLinkOffer(appLinkAction: String?, appLinkData: Uri?) {
  // 1
  if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
    // 2
    val promotionCode = appLinkData.getQueryParameter("code")
    if (promotionCode.isNullOrBlank().not()) {
      activityPromoBinding.discountGroup.visibility = View.VISIBLE
      activityPromoBinding.tvPromoCode.text = promotionCode
      // 3
      activityPromoBinding.btnClaimOffer.setOnClickListener {
        activityPromoBinding.tvOfferClaimed.visibility = View.VISIBLE
      }
    } else {
      activityPromoBinding.discountGroup.visibility = View.GONE
    }
  }
}

```

1. You check if the action from the intent is `ACTION_VIEW` and also if the intent has data.
2. Then you check if the link has a query parameter `code` and set the visibility of the discount UI to visible. You also display your promotion code to a `TextView`.
3. Then you show a `TextView` with an offer claimed message when you click `btnClaimOffer`.

You’re one step away from claiming the discount. :] To claim your discount, add the following code to the end of `onCreate`:

```kotlin
handleIntent(intent)
```

Here, you call `handleIntent(intent)` and pass the intent from `onCreate`.
