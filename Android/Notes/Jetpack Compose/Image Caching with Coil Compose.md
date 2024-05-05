# Image Caching with Coil Compose

W can request once for loading image then we can cache those images, not keeping them into device gallery but just cache them temporarily just in case we need them later.

There are two types of caching strategies:

1. **Disk Cache:**

2. **Memory Cache:** It keeps the image on the device's Ram. This strategy, nothing is safe persistently because you only access that image across the app relaunches but not when the app gets killed or user exits. Ram for specific app is cleared when the user exits. On the other hand, accessing the Ram is much faster than accessing the disk

By default, Coil strategy for caching is mixed between two strategies. Coil takes a look at the image and if it has certain size, it might be store at disk to not occupy too much memory, but if the image size is small, it stores the image on the Ram.

To execute a request for loading an image, coil uses `image loaders`.

`Image loaders` not only load the image, but alos handle all the caching process behid it.

> There is a default singleton image loader whenver you have access to the context, you can use it.
> 
> But we can change the default image loader for cross the app within application class.

Using custom image loader, we can have our own loading configurations and our own caching strategies

```kotlin
@HiltAndroidApp
class MyApplication : Application() , ImageLoaderFactory{
    override fun newImageLoader(): ImageLoader {
        
    }
}
```

You can use `callFactory` function when you are building ImageLoader object, when you want to intercept the loading request, for example loading image needs a user token. you can attach the token to the request header.

You can clear cached data whenever you want using this code:

```kotlin
imageLoader.diskCache?.clear()
imageLoader.memoryCache?.clear()
```

You can also remove a specific image using remove function which gives a key associated by that image url.

```kotlin
imageLoader.diskCache?.remove(imageUrl)
```


