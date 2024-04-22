# Difference between List and Array types in Kotlin

Array and List/ MutableList both are used to to store multiple values, but they have some key differences:

1. `Mutability`: an `array` is mutable, which means you can modify its element, on the other hand, `list` is immutable which means once a list is created, it cannot be changed. If you want a mutable list, you should use `mutableList`

2. `Size`: The size of `arrray` is fixed at the creation time, in contrast, the size of a `mutableList` can be change dynamically, elements can be added or removed.

3. `Performance`: Arrays are more efficient in terms of memory and performance because they don't have the overhead of object wrappers. However, lists provide more high-level operations and it easier to work with if you need to frequently add or remove elements. Array is a simple data structure that directly stores its elements while list and mutableList are interfaces that can have different implementations with additional overheads.
















