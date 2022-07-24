# Clean Architecture in Android — A Beginner Approach

> “**The goal of software architecture is to minimize the human resources required to build and maintain the required system**”

Well, probably if you are working on `a simple project Clean Architecture might seem overkill`, but what if you need to `decouple modules`, `testing the modules in isolation`, and helping your team to work on separated code containers? `Clean Architecture helps developers to avoid spelunking through the software code trying to figure out functionalities and business logic`.

# A bit of theory

![1_B7LkQDyDqLN3rRSrNYkETA.jpeg](C:\Users\Azin\Desktop\Files\Learning\Android\Articles\Clean%20Architecture%20in%20Android%20—%20A%20Beginner%20Approach\resources\1_B7LkQDyDqLN3rRSrNYkETA.jpeg)

- **Entities**: encapsulate enterprise-wide critical business rules. An entity can be an object with methods or a set of data structures and functions
- **Use cases:** orchestrate the flow of data to and from the entities
- **Controllers, Gateways, Presenters**: a set of adapters that convert data from the use cases and entities format to a most convenient way, in order to pass the data to the upper level (typically the UI)
- **UI, External Interfaces, DB, Web, Devices**: the outermost layer of the architecture, generally composed of frameworks such as database and web frameworks.

# A pragmatic approach

A typical android project normally needs to separate the concerns between the UI, the business logic, and the data model.

- **Domain**: contains the `definitions of the business logic of the app`, the `server data model`, `the abstract definition of repositories`, and the `definition of the use cases`. It’s a simple, pure kotlin module (android independent)
  
  ![1__Ip10ATck6xbtNzB4nIjAw.png](C:\Users\Azin\Desktop\Files\Learning\Android\Articles\Clean%20Architecture%20in%20Android%20—%20A%20Beginner%20Approach\resources\1__Ip10ATck6xbtNzB4nIjAw.png)

- **Data:** contains the `implementation of the abstract definitions of the domain layer`. Can be reused by any application without modifications. It contains `repositories` and `data sources implementations`, the `database definition` and its `DAOs`, the` network APIs definitions`, `some mappers to convert network API models to database models`, and vice versa.

![1_GF2ixAVa7dukw2-V5EeohA.png](C:\Users\Azin\Desktop\Files\Learning\Android\Articles\Clean%20Architecture%20in%20Android%20—%20A%20Beginner%20Approach\resources\1_GF2ixAVa7dukw2-V5EeohA.png)

- **App** (or presentation layer): it’s android specific and contains` fragments`, `view models`, `adapters`, `activities`, and so on. It also contains a service locator to manage dependencies, but you can use Hilt if you prefer.
  
  ![1_0jzCErFCZv_UcNwyU8d8bw.png](C:\Users\Azin\Desktop\Files\Learning\Android\Articles\Clean%20Architecture%20in%20Android%20—%20A%20Beginner%20Approach\resources\1_0jzCErFCZv_UcNwyU8d8bw.png)

# **The domain layer**

In the domain layer, we define the` data model`, the `use cases`, and the `abstract definition of the book repository`. The API returns a list of books, or volumes, with some info like title, authors, and image links

```kotlin
data class Volume(val id: String, val volumeInfo: VolumeInfo)
```

```kotlin
data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val imageUrl: String?
)
```

```kotlin
interface BooksRepository {

    suspend fun getRemoteBooks(author: String): Result<List<Volume>>

    suspend fun getBookmarks(): Flow<List<Volume>>

    suspend fun bookmark(book: Volume)

    suspend fun unbookmark(book: Volume)
}
```

```kotlin
class GetBooksUseCase(private val booksRepository: BooksRepository) {
    suspend operator fun invoke(author: String) = booksRepository.getRemoteBooks(author)
}
```

# The data layer

As we said before, the data layer must` implement the abstract definition of the domain layer`, so we need to put in this layer the repository concrete implementation. To do so, we can define two data sources, a “local” data source to provide persistence, a “remote” data source to fetch the data from the API.

```kotlin
class BooksRepositoryImpl(
    private val localDataSource: BooksLocalDataSource,
    private val remoteDataSource: BooksRemoteDataSource
) : BooksRepository {

    override suspend fun getRemoteBooks(author: String): Result<List<Volume>> {
        return remoteDataSource.getBooks(author)
    }

    override suspend fun getBookmarks(): Flow<List<Volume>> {
        return localDataSource.getBookmarks()
    }

    override suspend fun bookmark(book: Volume) {
        localDataSource.bookmark(book)
    }

    override suspend fun unbookmark(book: Volume) {
        localDataSource.unbookmark(book)
    }
}
```

it’s good practice to create some mappers to map the API response to the corresponding database entity. Remember that `we need the domain layer to be independent of the data layer, so we can’t directly annotate the domain **Volume** entity with **@Entity** room annotation`. We definitely need another class **BookEntity** and we are going to define a mapper between Volume and BookEntity.

```kotlin
class BookEntityMapper {
    fun toBookEntity(volume: Volume): BookEntity {
        return BookEntity(
            id = volume.id,
            title = volume.volumeInfo.title,
            authors = volume.volumeInfo.authors,
            imageUrl = volume.volumeInfo.imageUrl
        )
    }

    fun toVolume(bookEntity: BookEntity): Volume {
        return Volume(
            bookEntity.id,
            VolumeInfo(bookEntity.title, bookEntity.authors, bookEntity.imageUrl)
        )
    }
}
```

```kotlin
@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val authors: List<String>,
    val imageUrl: String?
)
```

# The presentation (or app) layer

The view model takes the use cases in its constructors and invokes the corresponding use case accordingly to user actions (get books, bookmark, unbookmark).

```kotlin
class BooksViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val bookmarkBookUseCase: BookmarkBookUseCase,
    private val unbookmarkBookUseCase: UnbookmarkBookUseCase,
    private val mapper: BookWithStatusMapper
) : ViewModel() {

    private val _dataLoading = MutableLiveData(true)
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _books = MutableLiveData<List<BookWithStatus>>()
    val books = _books

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _remoteBooks = arrayListOf<Volume>()

    // Getting books with uncle bob as default author :)
    fun getBooks(author: String) {
        viewModelScope.launch {
            _dataLoading.postValue(true)
            when (val booksResult = getBooksUseCase.invoke(author)) {
                is Result.Success -> {
                    _remoteBooks.clear()
                    _remoteBooks.addAll(booksResult.data)

                    val bookmarksFlow = getBookmarksUseCase.invoke()
                    bookmarksFlow.collect { bookmarks ->
                        books.value = mapper.fromVolumeToBookWithStatus(_remoteBooks, bookmarks)
                        _dataLoading.postValue(false)
                    }
                }

                is Result.Error -> {
                    _dataLoading.postValue(false)
                    books.value = emptyList()
                    _error.postValue(booksResult.exception.message)
                }
            }
        }
    }

    fun bookmark(book: BookWithStatus) {
        viewModelScope.launch {
            bookmarkBookUseCase.invoke(mapper.fromBookWithStatusToVolume(book))
        }
    }

    fun unbookmark(book: BookWithStatus) {
        viewModelScope.launch {
            unbookmarkBookUseCase.invoke(mapper.fromBookWithStatusToVolume(book))
        }
    }

    class BooksViewModelFactory(
        private val getBooksUseCase: GetBooksUseCase,
        private val getBookmarksUseCase: GetBookmarksUseCase,
        private val bookmarkBookUseCase: BookmarkBookUseCase,
        private val unbookmarkBookUseCase: UnbookmarkBookUseCase,
        private val mapper: BookWithStatusMapper
    ) :
        ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BooksViewModel(
                getBooksUseCase,
                getBookmarksUseCase,
                bookmarkBookUseCase,
                unbookmarkBookUseCase,
                mapper
            ) as T
        }
    }
}
```

The fragment is only observing the changes in the view model and detects the user actions on the UI.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksAdapter = BookAdapter(requireContext(), object : BookAdapter.ActionClickListener {
            override fun bookmark(book: BookWithStatus) {
                booksViewModel.bookmark(book)
            }

            override fun unbookmark(book: BookWithStatus) {
                booksViewModel.unbookmark(book)
            }
        })

        booksViewModel.getBooks("Robert C. Martin")
    }

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksViewModel.books.observe(viewLifecycleOwner, {
            booksAdapter.submitUpdate(it)
        })

        booksViewModel.dataLoading.observe(viewLifecycleOwner, { loading ->
            when (loading) {
                true -> LayoutUtils.crossFade(pbLoading, rvBooks)
                false -> LayoutUtils.crossFade(rvBooks, pbLoading)
            }
        })

        rvBooks.apply {
            layoutManager =
                GridLayoutManager(requireContext(), COLUMNS_COUNT)
            adapter = booksAdapter
        }

        booksViewModel.error.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                getString(R.string.an_error_has_occurred, it),
                Toast.LENGTH_SHORT
            ).show()
        })
    }
```

### Conclution

![1_OdrA2uYTDuT_I2qyp7yelQ.png](C:\Users\Azin\Desktop\Files\Learning\Android\Articles\Clean%20Architecture%20in%20Android%20—%20A%20Beginner%20Approach\resources\1_OdrA2uYTDuT_I2qyp7yelQ.png)

As you can see, each layer communicates only with the closest one, keeping inner layers independent from lower layers, this way, it’s easier to define tests properly in each module and the separation of concerns will help developers to collaborate on the different modules of the project.


