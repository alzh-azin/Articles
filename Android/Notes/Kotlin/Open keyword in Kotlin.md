# Open keyword in Kotlin

we mark class, function, or a variable with the `open` keyword to allow inheritance.

It means the class is open for extension and we can create a subclass of that `open` class.

Different in Kotlin and Java in inheritance.

| Java                     | Kotlin                  |
| ------------------------ | ----------------------- |
| `final class Mentor { }` | `class Mentor { }`      |
| `class Mentor { }`       | `open class Mentor { }` |

- In Kotlin, by default all the classes are final, it means they can't be extended.

- In Java, it is completely opposite. By default, All the classes in java are open for extension.



### open keyword with variable & function

Similar to the classes, all the functions in kotlin are by default final meaning that you cannot override them.

If you want to be able to override a function, you should mark that function with `open`

Similarly, the variables in kotlin are by default final and you should use `open` keyword to make them overrideable.


