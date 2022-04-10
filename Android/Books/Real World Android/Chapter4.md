## Interceptors

OKHttp lets you manipulate your requests and/or responses through interceptors,
which let you monitor, change or even retry API calls.

<img src="file:///C:/Users/azin.alizadeh/AppData/Roaming/marktext/images/2022-03-30-09-45-11-image.png" title="" alt="" data-align="center">

OkHttp allows two types of interceptors:

- Application interceptors:  Act between your code and OkHttp
- Network interceptors: Act between OkHttp and the server

here are two things to consider about NetworkUnavailableException:

1. It’s modeled as a **domain exception** 

2. It extends IOException. This is where the boundary between the layers starts to
   blur. It extends IOException because Retrofit only handles IOExceptions. So, if
   NetworkUnavailableException extends from any other type, the app is likely to
   crash. This implicitly couples the domain layer to the data layer. If, someday, the
   app stops using Retrofit in favor of a library that handles exceptions differently,
   the domain layer will change as well.

---

```kotlin
 override fun intercept(chain: Interceptor.Chain): Response
```

 Chain is the active chain of interceptors running when the request is ongoing

---

## Ordering the interceptors

```kotlin
OkHttpClient.Builder()
.addInterceptor(A)
.addInterceptor(C)
.addInterceptor(B)
```

The interceptors will run in that order: A → C → B.

---

## Managing API dependencies with Hilt

When you use Hilt, you don’t need to create Dagger components.

Hilt generates a hierarchy of predefined components with corresponding scope
annotations. These components are tied to Android lifecycles. This makes it a lot
easier for you to define the lifetime of your dependencies.

Define the component where you’ll install ApiModule by adding:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ApiModule
```

You’re installing the module in SingletonComponent. This component is the
highest in the component hierarchy — all other components descend from it. By
installing ApiModule here, you’re saying that any dependency it provides should live
as long as the app itself. Also, since each child component can access the
dependencies of its parent, you’re ensuring that all other components can access
ApiModule.
