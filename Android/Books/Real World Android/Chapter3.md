# Chapter 3: Domain Layer

### What is a domain layer?

The domain layer is the central layer of your app. It includes the code that describes
your domain space along with the logic that manipulates it.

- **entities:** Objects that model your domain space.

- **value objects:** Another kind of object that models your domain space.

- **interactors/use cases:** Logic to handle entities and/or value objects and produce a result.

- **repository interfaces:** Define contracts for data source access.

This layer encompasses the business logic of the app. Your business logic is one of
the most important parts of your app, as it defines how the app works. The less you
mess with it, the better! That’s why the domain layer shouldn’t depend on other
layers.
For example, imagine you change your data layer by migrating from REST to
GraphQL. Or you change your presentation layer by migrating the UI to Jetpack
Compose. None of those changes have anything to do with the business logic. As
such, they shouldn’t affect the domain layer at all.

### Do you really need a domain layer?

- Keeping your code clean and easy to maintain by focusing the business logic in
  one layer only. Single responsibility code is easier to manage.

- Defining boundaries between code that implements app logic and code that has
  nothing to do with that logic, like UI or framework code. Given how fast the
  Android framework changes, this separation is critical.

- Easing the onboarding of future developers, who can study the layer to understand
  how the app works.

### Entities & value objects

- Entities have an ID that allows you to tell them apart. Their properties can
  =======

- Entities have an` ID` that allows you to tell them apart. Their properties can
  
  > > > > > > > 2fa4b624fd5a3bf6840353d71696576435021d9b
  > > > > > > > change, but the ID always remains the same.

- Value objects describe `some aspect of an entity`. They don’t have IDs, and if you
  change one of their properties, you create a new value object. For this reason, they
  should always be immutable.

### What should you model?

Frequently, apps are built to support a pre-existing business. In these cases, the
domain model already exists somewhere — typically in the back end. Therefore,
reproducing the back end’s domain model is usually enough.

### Adding the animal entities

Another important thing to mention in Photo is its companion object — more 
specifically, EMPTY_PHOTO. This property represents the empty state of a Photo. It’s a 
simplified version of the Null Object Pattern, and it’s a nice way to avoid null 
values

```kotlin
data class Photo(
    val medium: String,
    val full: String
) {
    companion object {
        const val EMPTY_PHOTO = ""
    }

    fun getSmallestAvailablePhoto(): String { // 1
        return when {
            isValidPhoto(medium) -> medium
            isValidPhoto(full) -> full
            else -> EMPTY_PHOTO
        }
    }

    private fun isValidPhoto(photo: String): Boolean { // 2
        return photo.isNotEmpty()
    }
}
```

### To type or not to type

Look at Animal again

```kotlin
data class Animal(
 val id: Long,
 val name: String,
 val type: String,
 val tags: List<String>
)
```

These properties all have one thing in common: None of them have specific domain 
types. In fact, they’re just a mix of standard types from the language

When modeling your domain, `you need to make some choices, and those choices 
have trade-offs`. One of the hardest choices to make is how many new domainspecific types you should create.

`Types provide safety and robustness in exchange for complexity development time`. For instance, what’s keeping you from creating Animal with the id of -1L? It’s just a Long type. It doesn’t care about the value you set it to, as long as it’s of type Long.
However, adding a new type called Id changes things:

```kotlin
data class Id(val value: Long) {
    init { // 1
        validate(value)
    }

    private fun validate(id: Long) {
        if (id.hasInvalidValue()) { // 2
            throw InvalidIdException(id)
        }
    }
}
```

Now, imagine that Id has a specific format. Then, you need to add a new validation:

```kotlin
private fun validate(id: Long) {
    if (id.hasInvalidValue()) {
        throw InvalidIdException(id)
    }
    if (id.hasInvalidFormat()) {
        throw InvalidIdFormatException(id)
    }
}
```

You also change from a chain of if conditions to a when.
It looks clean, but it now throws a bunch of exceptions. You start worrying that it 
might be hard to maintain the code in the future, especially if you add new validation 
rule.
So, you refactor:

```kotlin
private fun validate(id: Long): Either<IdException, Boolean> { // 1
    return when {
        id.hasInvalidValue() -> Left(InvalidIdException(id))
        id.hasInvalidFormat() -> Left(InvalidIdFormatException(id))
        id.exceedsLength() -> Left(InvalidIdLengthException(id))
        else -> Right(true)
    }
}

sealed class Either<out A, out B> { // 2
    class Left<A>(val value: A) : Either<A, Nothing>()
    class Right<B>(val value: B) : Either<Nothing, B>()
}

sealed class IdException(message) : Exception(message) { // 3
    data class InvalidIdException(id: Long) : IdException("$id")
    data class InvalidIdFormatException(id: Long) :
        IdException("$id")

    data class InvalidIdLengthException(id: Long) :
        IdException("$id")
}
```

Here’s what’s happening, step by step:

1. You change the method’s signature to be explicit about what’s happening inside.

2. You create the Either sealed class, a disjoint union to represent success and 
   failure values

3. You encapsulate all the exceptions in the IdException sealed class.

Dealing with Booleans is also fun. For instance, consider this class:

```kotlin
class User(name: String, email: String, isAdmin: Boolean)
```

You can see where this is going, can’t you? `That isAdmin is a disaster waiting for the worst moment possible to explode in your face`. A simple mistake or a bug that makes the property true when it should be false can completely wreck your app.
A common way to avoid stuff like this is to use inheritance:

```kotlin
open class User(name: String, email: String)

class Admin(name: String, email: String) : User(name, email)
```

It’s up to you and your team to decide. Do you want to follow a straightforward, 
“we’ll refactor when we get there”, YAGNI (You Aren’t Gonna Need It) approach? Or 
a more time-consuming, type-safe, “model all the things!” way of doing things?
In general, a solution somewhere in the middle, with just enough design upfront, will 
fit your needs the best.

### Testing your domain logic

A good way to organize your tests is to mimic the package structure of the app code.
This makes it possible for tests to access any internal properties of the code, since anything with the internal visibility modifier is only accessible to code in the same
package.

 you should keep your tests small and focus on one thing at a time. If you do, it won’t be too hard to keep the comments in place.

### Key points

- Entities have an identity that allows you to distinguish between them.

- Value objects enrich your domain and can either contain entities or be contained
  by them.

- Defining how many custom types to have in your domain is something you should
  consider carefully. Try to find a balance between under typing and over typing, as
  both are troublesome.

- Be careful when adding logic to your app that relates to your domain model: That
  logic might belong inside the actual model classes.

- Repository interfaces allow for dependency inversion, which is essential to keep
  the domain layer isolated.

- Test behavior, not code or data.
