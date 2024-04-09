# AssociateBy - List to Map in kotlin

This function returns a list into a list.

```kotlin
data class Contact(val name: String, val phoneNumber: String)
```

```kotlin
val contacts = listOf(
    Contact("Amit", "+9199XXX11111"),
    Contact("Messi", "+9199XXX22222"),
    Contact("Ronaldo", "+9199XXX33333"))
```

```kotlin
val nameToNumberMap = contacts.associateBy( {it.name}, {it.phoneNumber})
```

Notes:

- It has two arguments, `keySelector` and `valueTransform` are index and value

- If two elements have  the same key when they are added to the list, the last one will be added to the list.

- Maintain the original order of items.
