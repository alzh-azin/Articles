# Chapter 4: Data Layer - Network

The data layer is where you put the code responsible for interacting with your data
sources. An app can have multiple data sources, and they can change over time. For instance, you can migrate from a REST server to a GraphQL server, or from a Room database to a Realm database. These changes only matter to the data handling logic, and should not affect the code that needs the data.

### The repository pattern

The repository is just an abstraction over the way you access data. It creates a thin
layer over data sources — a class that wraps up calls to the objects that do the heavy
lifting. While this sounds a bit redundant, it has its purposes. It lets you:

- Swap data sources without affecting the rest of the app. Swapping sources is rare,
  but trust me, it happens. :]

- Create the boundary between the data layer and the other layers that need to
  operate on data.

![Capture2.PNG](C:\Users\azin.alizadeh\Desktop\Learning\Android\Books\Real%20World%20Android\resources\Capture2.PNG)

You can have as many repositories as you want. A popular choice is to have one
repository per domain entity type.

### Network data models

```kotlin
@JsonClass(generateAdapter = true) //1
data class ApiBreeds(
    @field:Json(name = "primary") val primary: String?, //2
    @field:Json(name = "secondary") val secondary: String?,
    @field:Json(name = "mixed") val mixed: Boolean?,
    @field:Json(name = "unknown") val unknown: Boolean?
)
```

- This annotation decorates every class. The app uses Moshi to parse the JSON
  from API responses. `This annotation lets Moshi know it can create an object of this type from JSON data`. `Moshi will also automagically create an adapter if you set generateAdapter to true. It’ll then use it to create an instance of the class`. Without this parameter, you’ll get a runtime error from Moshi, unless you create the adapter yourself.

- There are two different things to notice here. First, the Moshi annotation maps
  the JSON variable called primary to the code variable called primary. In this
  case, you didn’t need the annotation because the names are the same. Still, it’s
  there for consistency’s sake.` Second, you used a nullable type. Long story short, never trust your backend. :] Using nullable types ensures that even if something goes wrong and you get unexpected nullable values in the response, the app won’t crash.`

## Mapping data to the domain

```kotlin
interface ApiMapper<E, D> {
    fun mapToDomain(apiEntity: E): D
}
```

Having all the mappers follow this interface gives you the advantage of decoupling
the mapping. This is useful if you have a lot of mappers and want to make sure they
all follow the same contract.

```kotlin
override fun mapToDomain(apiEntity: ApiAnimal):AnimalWithDetails {
    return AnimalWithDetails(
        id = apiEntity.id
        ?: throw MappingException("Animal ID cannot be null"), // 1
        name = apiEntity.name.orEmpty(), // 2
        type = apiEntity.type.orEmpty(),
        details = parseAnimalDetails(apiEntity), // 3
        media = mapMedia(apiEntity),
        tags = apiEntity.tags.orEmpty().map { it.orEmpty() },
        adoptionStatus = parseAdoptionStatus(apiEntity.status),
        publishedAt =
        DateTimeUtils.parse(apiEntity.publishedAt.orEmpty()) //4
)
}
```

1. If the API entity doesn’t have an ID, the code throws a MappingException. You
   need IDs to distinguish between entities, so you want the code to fail if they don’t
   exist.

2. If name in the API entity is null, the code sets the name in the domain entity to
   empty. Should it, though? CanAnimalWithDetails entities have empty names?
   `That depends on the domain.` `In fact, mappers are a good place to search for domain constraints.` Anyway, for simplicity, assume an empty name is possible.

3. details is a value object, so `the code delegates its creation to an appropriate
   method`. Clean code keeps responsibilities well separated.

## Interceptors

OKHttp lets you manipulate your requests and/or responses through interceptors,
which let you monitor, change or even retry API calls.

![OkHttp Interceptors](resources/okhttp_interceptors.png)

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

### AuthenticationInterceptor

```kotlin
 override fun intercept(chain: Interceptor.Chain): Response
```

 Chain is the active chain of interceptors running when the request is ongoing

#### Checking the token

```kotlin
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferences.getToken()
        val tokenExpirationTime = Instant.ofEpochSecond(preferences.getTokenExpirationTime())

        val request = chain.request()

//4     if (chain.request().headers[NO_AUTH_HEADER] != null) return
        return chain.proceed(request)
    }
```

4. This is a special case for requests that don’t need authentication. Say you have a
   login request, for instance. You can add a custom header to it in the API interface
   — like NO_AUTH_HEADER — then check if the header exists here. If so, you let the
   request proceed. You won’t need this logic in this case, but it’s good to be aware
   of it.

You might find the access to preferences weird. Typically, a repository mediates
between the different data sources, while they remain unaware of each other. `Its
purpose in this layered architecture is to pass the other layers the data they need.`

In this case, though, all the action happens inside the data layer itself. You’d be
introducing `accidental complexity` by creating a `circular dependency` between the API and the repository code. Also, Preferences is an interface, so the
implementation details are still decoupled. You must resist `convention triggered`
over-engineering. :]

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

### Defining dependencies

- @Inject: Use in class constructors to inject code you own, such as the data mappers.

- @Provides: Use in modules to inject code you don’t own, like any library instance.

- @Binds: Use in modules to inject interface implementations when you don’t need
  initialization code.

```kotlin
@Provides
@Singleton
fun provideApi(okHttpClient: OkHttpClient): PetFinderApi
```

@Provides works as it does in traditional Dagger. @Singleton, on the other hand, is
the scope annotation for SingletonComponent. You can only add annotations to a
module that match the scope of the component. If you try to use other scope
annotations, you’ll get a compile-time error. You won’t get any errors if you try that
now though, because your code doesn’t request PetFinderApi yet.

there are also two important details about OKHttp that you have to consider:

- Each OkHttp instance has its own thread pool, which is expensive to create.

- OkHttp has a request cache on disk. Different OkHttp instances will have
  different caches.

### Key points

- A data layer keeps your data I/O organized and in one place.

- The repository pattern is great for abstracting data sources and providing a clear
  boundary around the data layer.

- 
