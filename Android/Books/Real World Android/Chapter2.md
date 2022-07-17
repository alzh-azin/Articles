# Chapter 2: Starting from the beginning

### Package by feature approach

 Expand the com.raywenderlich.android.petsave package. Did you hear that? **That was the project screaming its purpose at you!**

This project is organized in a package by feature structure. `Everything that’s related to a feature`, and only to that feature, is stored inside the same package

 Code `shared by two or more features` is stored in separate common packages.

This type of package organization has a few advantages:

1. Just by looking at the package structure, you easily get a feeling for what the app
   does. Some people also like to call this a `screaming architecture`

2. You end up with packages that `not only have high cohesion`, they’re also either
   `loosely coupled` or `completely decoupled` from one another

### High cohesion

Cohesion refers to the relationship between different programming elements. `The
stronger the connection between code inside a component, the more cohesive that component is.`

For instance, imagine you have a class that’s responsible for applying a cute filter to
a cat’s picture, called CatFilter.

```kotlin
        class CatFilter(private val picture: Picture) {
            // properties related to filter and picture state
            // ...
            private fun parsePixels() {
            // store individual pixels and relationships between them
            }

            private fun filterPixels() {
            // apply the filter to each pixel
            }

            private fun smoothenResult() {
            // apply picture smoothing techniques
            }

            fun apply(): Picture {
            // use methods above
            }
            // other methods
      } 
```

The methods and properties of this class are all closely related to each other, which
means that the class is highly cohesive.

Now, imagine the case where you start adding more responsibilities to the class. Not
only does CatFilter apply a filter, but now it also saves and loads the result with the
help of the file system. `You’ll start having elements in the class that have nothing to do with each other` — parsePixels() and save(picture: Picture) have
completely different purposes.
In other words, <mark>your class will now have a lower degree of cohesion</mark>.

### Low coupling

Coupling has to do with dependencies between programming elements.

```kotlin
    class CatPictureFileSaver {
        fun save(picture: Picture) {
        // file writing code.
        // Calls compression and encoding methods.
        }

        fun load(picturePath: String): Picture {
        // file reading code
        // Calls decompression and decoding methods.
        }

        private fun compress(picture: Picture): CompressedPicture {
        // fancy compression algorithm
        }

        private fun encode(
            compressedPicture: CompressedPicture
        ): ByteArray {
        // byte encoding
        }
        // other methods
    }
```

This new class also has methods to compress/decompress and encode/decode the
image, which are strongly related to its purpose. Nice, now CatFilter and
CatPictureFileSaver are two highly cohesive classes!

After some time, requirements change. You now have to cache the intermediate
results of the filtering. To implement this, you call the persistence methods of
CatPictureFileSave directly in a few different places in CatFilter.

This may seem like the logical way to accomplish your goals but, `by doing so, you’re forcing CatFilter to be tightly coupled with CatPictureFileSaver`. Consider a
scenario where a requirement change dictates that you drastically change or even
remove CatPictureFileSaver. `Due to the coupled nature of the classes, you’d have to make significant changes to CatFilter as well.`

On the other hand, if you have something like a CatPictureSaver interface that
CatPictureFileSaver extends, and have CatFilter depend on it, then the classes
would be loosely coupled. `Changes to CatPictureFileSaver that don’t affect this interface would likely not affect CatFilter at all.`

```kotlin
interface CatPictureSaver {
    fun save(picture: Picture)
    fun load(picturePath: String): Picture
}

class CatPictureFileSaver : CatPictureSaver {
    // interface overrides and private methods/properties
}

class CatFilter(
    private val picture: Picture,
    private val pictureSaver: CatPictureSaver
) {
    // code...
    private fun filterPixels() {
        // ...
        // CatFilter knows nothing about save method's inner
        // workings!
        pictureSaver.save(filteredPicture)
        // ...
    }
    // more code...
}
```

The interface would ideally use `generic naming` to keep your implementation options
open.

### Aiming for orthogonality

 Generally speaking, these patterns improve software by:

- Making it easier to maintain, less risky to change and more future proof.

- Allowing it to be orthogonal. When software is orthogonal, `you can change its
  components freely without affecting the other components’ behavior`. This is
  possible because, inside each component, the code is closely related (cohesion)
  and doesn’t depend directly on other components (coupling).

A good way to achieve cohesion is to ensure your code components each have a
**single responsibility.** To keep components decoupled, you can use things like
**interfaces or polymorphism**. In other words, **by following the SOLID principles,
you’ll automatically follow these two principles as well.**

be warned:
Software principles are **addictive**. As with design patterns, you can easily get carried
away and start applying the principles to every corner of your codebase.
If you go down this rabbit hole, you’ll end up with an **over-engineered** app that lost
track of its initial purpose. Remember: all things in moderation.

### Full stack features through layers

- **domain:** Home to all the `use cases`, `entities` and`value objects` that describe the domain of the app.

- **data:** Layer responsible for enabling all the `interactions with data sources`, both internal, like `shared preferences` or the `database`, and external, like a `remote API.s`

- **presentation:** Where the Android framework does most of its heavy lifting,
  `setting up the UI and reacting to user input`.

### Boundaries between layers

![Capture1.PNG](C:\Users\azin.alizadeh\Desktop\Learning\Android\Books\Real%20World%20Android\resources\Capture1.PNG)

It’s especially common for projects that follow clean architecture to display such
boundaries. Clean architecture goes the extra mile, inverting the dependencies to
ensure that they only flow inwards toward the domain layer at the center. In other
words, the domain layer never depends on other layers.

### Why use layered features?

If you use it right, packaging by layer can be beneficial. Packaging by feature on the outside and by layer on the inside enables you to:

- Have highly cohesive, loosely coupled code throughout your app.

- Reduce your cognitive load when dealing with each layer. For instance, you can
  change the domain layer of a feature while mostly ignoring the presentation and
  data layers, due to the interface boundaries.

- Test entire layers by replacing other layer dependencies with mocks or fakes.

- Easily refactor a layer’s implementation without messing with the other ones. This
  is somewhat rare, but it does happen.

Anything that’s shared between different features will live in the **common package**.

### Mastering your domain

The domain is the subject area the software applies to or provides a solution to. In other words, it’s the environment you extract the requirements from, and it’s where the app’s features act.

### Key points

- Packaging by feature enables you to work out what an app does just by looking at
  the packages.

- You should strive for high cohesion and low coupling on every level of your apps.

- Separating the code of each feature by internal layers is an excellent way to have
  maintainable and flexible code, while also achieving high cohesion and low
  coupling.

- Being familiar with your app’s domain enables you to better understand what it
  does and what its users expect.

- Putting yourself in your users’ shoes helps you identify potential problems with
  your app.

- 
