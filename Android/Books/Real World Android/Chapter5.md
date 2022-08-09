## Adding foreign keys

```kotlin
@Entity(
    tableName = "photos",
    foreignKeys = [
      ForeignKey(
          entity = CachedAnimalWithDetails::class, // 1
          parentColumns = ["animalId"], // 2
          childColumns = ["animalId"], // 3
          onDelete = ForeignKey.CASCADE // 4
      )
     ],
     indices = [Index("animalId")] // 5
)
```

1. Specifies the entity that the foreign key belongs to

2. Defines the column that matches the foreign key in the parent table

3. Defines the column in this table where you find the foreign key

4. Instructs Room to delete the entity if the parent entity gets deleted

5. Sets the foreign key as an indexed column.

Setting columns as indices is a way for Room to locate data more quickly. Here, you
set animalId as an index because it’s a foreign key. If you don’t set a foreign key as
an index, changing the parent table might trigger an unneeded full table scan on
the child table, which slows your app down. Fortunately, Room throws a compile-
time warning if you don’t index the key.

Having indices speeds up SELECT queries. On the other hand, it slows down INSERTs
and UPDATEs. This is nothing to worry about with PetSave, as the app will mostly read
from the database.

## Setting up your one-to-many relationships

1. Create one class for the parent and another for the child entity. You already have
   these.

2. Create a data class representing the relationship.

3. Have an instance of the parent entity in this data class, annotated with
   @Embedded.

4. Use the @Relation annotation to define the list of child entity instances.

```kotlin
data class CachedAnimalAggregate(
    @Embedded // 1
    val animal: CachedAnimalWithDetails,
    @Relation( 
    parentColumn = "animalId", 
    entityColumn = "tag"
    )
    val tags: List<CachedTag>
    ) {
        // ...
      }
```

## Setting up the many-to-many relationship

you need to create a:

1. Class for each entity (already done).

2. Third class to cross-reference the two entities by their primary keys.

3. Class that models the way you want to query the entities. So, either an animal
   class with a list of tags, or a tag class with a list of animals.

```kotlin
data class CachedAnimalAggregate(
    @Embedded 
    val animal: CachedAnimalWithDetails,
    @Relation(
    parentColumn = "animalId", 
    entityColumn = "tag",
    associateBy =
    Junction(CachedAnimalTagCrossRef::class) //1
    )
    val tags: List<CachedTag>
    ) {
        // ...
      }
```

1. Use associateBy to create the many-to-many relationship with Room. You set it
   to a Junction that takes the cross-reference class as a parameter. As you can see
   from the entity relationship diagram, the cross-reference class is
   CachedAnimalTagCrossRef.

## Implementing the cross-reference table

```kotlin
@Entity(
    primaryKeys = ["animalId", "tag"], 
    indices = [Index("tag")] // 1
)
data class CachedAnimalTagCrossRef(
val animalId: Long,
val tag: String
)
```

1. `While primary keys are indexed by default`, you’re explicitly indexing tag, and
   tag only. You need to index both, because you use both to resolve the
   relationship. Otherwise, Room will complain.

If you change the primary key above to ["tag", "animalId"], then you’d have to index animalId instead of tag

```kotlin
@Entity(
primaryKeys = ["tag", "animalId"], // HERE
indices = [Index("animalId")])
data class CachedAnimalTagCrossRef(
    val animalId: Long,
    val tag: String
)
```

## Another way of data mapping

```kotlin
@Entity(tableName = "photos")
data class CachedPhoto(
// ...
) {
   companion object {
      fun fromDomain(
          animalId: Long,
          photo: Media.Photo): CachedPhoto { // HERE
       val (medium, full) = photo
       return CachedPhoto(
          animalId = animalId,
          medium = medium,
          full = full)
      }
   }
  // ...
}
```

In this code, fromDomain returns a CachedPhoto instance, which it builds from a
domain Photo and the corresponding animalId. It has to be a companion object
function due to dependencies. To make it a class member function, you’d have to
add it to Photo, `which would make the domain aware of the data layer.`

You could also achieve the same result with an extension function, as long as it
extends CachedPhoto. In the end, both options boil down to static functions.

Cache models have toDomain and fromDomain functions, while API models only
have toDomain. That’s because you won’t send anything to the API, so there’s no
need to translate domain models into API DTOs.
