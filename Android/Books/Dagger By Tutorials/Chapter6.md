# Chapter 6: Hello Dagger

## Building the “RayDi” DI framework

Your first step is to mark the repository property as an entry point for the Server
by using the @RayInject annotation, like this:

```kotlin
@RayDi
class Server() {                
    @RayInject
    lateinit var repository: Repository

    fun receive(data: Data) {
        repository.save(data)
    }
}
```

You also use the @RayDi annotation to tell RayDi that Server is a class you want to
add to the dependency graph for the app.

There’s a problem though: Repository is an interface and RayDi doesn’t know
which implementation to use. You can help it by using the @RayBind annotation, like
this:

```kotlin
@RayBind(Repository::class)
class FakeRepository : Repository {            
    var collection: MutableList<Data> = mutableListOf()
    override fun save(data: Data) {
    collection.add(data)
    println("Added $data")
    }
}
```

With this code, you tell the RayDi tool that whenever you want to inject an object of
type Repository, you need to create an instance of FakeRepository. Now, the
RayDi framework has all the information it needs to create the code for the
injection

### Annotation

```kotlin
// 1
@Retention(AnnotationRetention.SOURCE)
// 2
@Target(AnnotationTarget.CLASS)
// 3
annotation class RayBind(val interfaceClazz: KClass<*>)
```

1. Use @Retention to tell the compiler that the information about this annotation
   should persist at the source code level so it can be removed at bytecode and
   runtime. That’s because the compiler is the only one that will use the annotation.

2. Tell the compiler that it can only use @RayBind when it applies to a class. You
   can’t apply that annotation to a field, property or any other construct. If you try,
   you’ll get a compilation error.

3. Define @RayBind with a mandatory parameter named interfaceClazz and the
   type KClass<*>. This attribute sets the type of interface the annotated class
   implements.

```groovy
apply plugin: 'kotlin-kapt'
```

kotlin-kapt plugin lets you use the annotation processor in Kotlin to add all the required tasks.

## Installing Dagger

#### @Component

For Dagger to be able to provide a reference to an instance of the Server class, you
just need to create a simple interface

```kotlin
// 1
@Component
// 2
interface AppComponent {
    // 3
    fun server(): Server
}
```

1. It must contain @Component, which you use to define classes with factory
   responsibility

2. When you create a @Component, you don’t need to define any concrete class — an
   interface is enough. This is a way to tell Dagger that you just want to get an
   instance of a given type, along with all its dependencies, without knowing the
   details about how Dagger does its job.

3. The return type of the operation you define in the @Component interface is the
   only thing that really matters. The only reason the name of the operation is
   important is to make your code more readable.

with this simple definition, you’ve started to talk to Dagger, telling
it that you need an instance of a Server

#### @Inject

In the previous paragraph, you learned how to use @Component to tell Dagger what
you need. You asked for a Server, but Dagger doesn’t know how to create one. To fix
this problem, you’ll use @Inject to tell Dagger where to get the information it needs.

In this case, you’ll use @Inject to tell Dagger how to create an instance of Server.
Start by opening Server.kt and annotating Server, as in the following code:

```kotlin
class Server @Inject constructor() { // HERE
    lateinit var repository: FakeRepository
    fun receive(data: Data) {
        repository.save(data)
    }
}
```

Nobody told Dagger to inject the FakeRepository into the Server instance it provides. How would it know?
You’ll solve the problem using @Inject

> lateinit property repository has not been initialized

```kotlin
@Inject // HERE
lateinit var repository: FakeRepository
```

Here, you simply use @Inject for lateinit var repository.

## Using @Module and @Provides

```kotlin
// 1
@Module
object MainModule {
    // 2
    @Provides
    fun provideRepository(): Repository = FakeRepository()
 }
```

1. Create an object annotated with @Module. A module is a way to give Dagger
   additional information about the dependency graph.
2. Define provideRepository() and annotate it with @Provides. In this case, the
   name of the function is important only for readability. The only thing that
   matters here is the return type. Here, you’re telling Dagger that every time it
   needs to inject an object of type Repository, it should invoke this function to
   get one. This function provides the instance of type Repository Dagger needs.

Here, it’s important to note that Dagger is no longer responsible for creating the
instance of FakeRepository. You do that in the provideRepository() function
implementation instead.

Repository is an interface. to provide an instanse of fakeRepository, you don’t need @Inject in extended class anymore.

The simple definition of @Module is not enough. You still need to tell Dagger which
@Component uses it.

```kotlin
@Component(modules = arrayOf(MainModule::class)) // HERE
interface AppComponent {
    fun server(): Server
}
```

AppComponent is the object that provides you the reference to the Server instance
with all its dependencies. It needs to know how to create the object of type
Repository to inject.

Using the modules attribute of @Component, you tell Dagger where to find the
information it needs: MainModule.

## Key points

- Dagger is just a code generation tool, helping you implement the dependency
  injection pattern in your project.

- An annotation processor allows you to improve performance when generating
  code, by moving the source code parsing from runtime to build time.

- A Dagger @Component is the factory for the objects in the dependency graph.

- You can use @Inject to tell Dagger how to create an instance of a class.

- @Inject also lets you tell Dagger what property to inject.

- @Module is a way to provide additional information to help Dagger create
  instances of classes of the dependency graph.

- @Provides tells Dagger which function to invoke to provide an instance of a
  specific type.
