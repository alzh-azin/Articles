# Remove duplicates from an array in kotlin

There are different way to remove duplicate element in an array in kotlin

Consider a data class Mentor like below:

```kotlin
data class Mentor(val id: Int, val name: String)
```

And, an array of Mentor:

```kotlin
val mentors = arrayOf(
    Mentor(1, "Amit Shekhar"),
    Mentor(2, "Anand Gaurav"),
    Mentor(1, "Amit Shekhar"),
    Mentor(3, "Lionel Messi"))
```

### Remove duplicates using distinct()

```kotlin
val distinct = mentors.distinct()
println(distinct)
```

output:

```kotlin
[Mentor(id=1, name=Amit Shekhar),
Mentor(id=2, name=Anand Gaurav),
Mentor(id=3, name=Lionel Messi)]
```

Notes:

1. Maintains the original order of items.

2. Among the equal elements in the array, the first one is present in the list.

3. Return a `List`



### Remove duplicates using toSet()

```kotlin
val toSet = mentors.toSet()
println(toSet)
```

Output:

```kotlin
[Mentor(id=1, name=Amit Shekhar),
Mentor(id=2, name=Anand Gaurav),
Mentor(id=3, name=Lionel Messi)]
```

Notes:

1. Maintains the original order of items.

2. Returns a set which is a `read-only` set.

### Remove duplicates using toMutableSet()

```kotlin
val toMutableSet = mentors.toMutableSet()
println(toMutableSet)
```

Output:

```kotlin
[Mentor(id=1, name=Amit Shekhar),
Mentor(id=2, name=Anand Gaurav),
Mentor(id=3, name=Lionel Messi)]
```

Notes:

1. Maintains the original order of items.

2. Retruns a mutableSet which is a `read/write` set.

### Remove duplicates using toHashSet()

```kotlin
val toHashSet = mentors.toHashSet()
println(toHashSet)
```

Output:

```kotlin
[Mentor(id=3, name=Lionel Messi),
Mentor(id=1, name=Amit Shekhar),
Mentor(id=2, name=Anand Gaurav)]
```

Notes:

1. Similar to `MutableSet` but do NOT maintain the original order of items.

2. Return `hashSet`


