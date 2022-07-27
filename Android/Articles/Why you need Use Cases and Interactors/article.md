# Why you need Use Cases/Interactors

![Capture.PNG](.\Resources\Capture.PNG)

# Layered Architectures and the Application Layer

![Capture1.PNG](.\Resources\Capture1.PNG)

## Separation of concerns

One of the primary goals of any architecture is the `separation of concerns.`

1. **Presentation**: Responsible for showing information to the user and interpreting user commands;
2. **Application**: Defines the jobs the software is supposed to do by orchestrating the data flow from and to the domain models;
3. **Domain**: Represents concepts of the business, information about the current situation and business rules;
4. **Data**: Responsible for persisting domain models.

## The 2 “business logics”

Domain and Application layers both represent business logic, however, their nature is of 2 kinds:

1. **Domain business logic**: here you find the “models” of your app, which can be of different types (Aggregate Roots, Entities, Value Objects) and that implement enterprise-wide business rules (they not only contain data but also processes).
2. **Application business logic**: here you find the so-called “services”/“usecases”/“interactors”, situated on top of models and the “ports” for the Data Layer (used for dependency inversion, usually **Repository interfaces**), they retrieve and store domain models by using either repositories or other Use Cases.

In ***PresentationDomainDataLayering*** each package represents a different topic: UI logic, business logic, and persistence logic.

Because Domain and Application cover the same topic and since the Application layer is “thin” they are often grouped together, however, there is still a separation at the class level and/or at a sub-package level:

![1_aiQ9uRG1LtuiPu6fTcFCjw.png](.\Resources\1_aiQ9uRG1LtuiPu6fTcFCjw.png)

# Avoid “God” Presenters/ViewModels

What happens when we don’t separate properly the concerns? The answer is “we have god objects”.

Let’s have a look at ***a ViewModel without Use Cases*** vs ***a ViewModel with Use Cases***.

## Without Use Cases

The ViewModel:

```kotlin
class TransactionsViewModelImpl(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : TransactionsViewModel, ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    override val transactions = MutableLiveData<List<Transaction>>()
    override val showProgress = MutableLiveData<Boolean>()
    override val showError = MutableLiveData<Boolean>()
    override val showContent = MutableLiveData<Boolean>()

    override fun loadTransactions() {
        when (val result = userRepository.getUser()) {
            is Result.Success -> loadUserTransactions(result.value)
            is Result.Failure -> setErrorState()
        }
    }

    private fun loadUserTransactions(user: User) {
        setLoadState()
        transactionRepository.getUserTransactions(user)
            .subscribeBy {
                handleResult(it)
            }.addTo(compositeDisposable)
    }

    private fun setLoadState() {
        showProgress.postValue(true)
        showError.postValue(false)
        showContent.postValue(false)
    }

    private fun handleResult(result: Result<List<Transaction>>) {
        when (result) {
            is Result.Success -> setContentState(result.value)
            is Result.Failure -> setErrorState()
        }
    }

    private fun setContentState(transactionsResult: List<Transaction>) {
        showContent.postValue(true)
        transactions.postValue(transactionsResult)
        showProgress.postValue(false)
        showError.postValue(false)
    }

    private fun setErrorState() {
        showError.postValue(true)
        showProgress.postValue(false)
        showContent.postValue(false)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}
```

The dependencies:

```kotlin
// A wrapper for handling failing requests
sealed class Result<T> {

    data class Success<T>(val value: T) : Result<T>()

    data class Failure<T>(val throwable: Throwable) : Result<T>()

}

// The models (simplified)
data class User(val id: String)
data class Transaction(val id: String, val amount: Float)

// The repository for the transactions
interface TransactionRepository {
    fun getUserTransactions(user: User): Single<Result<List<Transaction>>>
}

// The repository for the user
interface UserRepository {
    fun getUser(): Result<User>
}
```

## With Use Cases

Let’s now add the Use Cases:

```kotlin
// User
interface GetCurrentUserUseCase {
    operator fun invoke(): Result<User>
}

class GetCurrentUserUseCaseImpl(
    private val userRepository: UserRepository
) : GetCurrentUserUseCase {
    override fun invoke(): Result<User> = userRepository.getUser()
}

// Transaction
interface GetUserTransactionsUseCase {

    operator fun invoke(): Single<Result<List<Transaction>>>

}

class GetUserTransactionsUseCaseImpl(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val transactionRepository: TransactionRepository
) : GetUserTransactionsUseCase {

    override fun invoke(): Single<Result<List<Transaction>>> {
        return when (val result = getCurrentUserUseCase()) {
            is Result.Success -> transactionRepository.getUserTransactions(result.value)
            is Result.Failure -> Single.just(Result.Failure(result.throwable))
        }
    }

}
```

All the previous dependencies stay as before with the only difference that they are now used by the Use Cases instead of the ViewModels.  
And now the ViewModel:

```kotlin
class TransactionsViewModelImpl(
    private val getUserTransactionsUseCase: GetUserTransactionsUseCase
) : TransactionsViewModel, ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    override val transactions = MutableLiveData<List<Transaction>>()
    override val showProgress = MutableLiveData<Boolean>()
    override val showError = MutableLiveData<Boolean>()
    override val showContent = MutableLiveData<Boolean>()

    override fun loadTransactions() {
        setLoadState()
        getUserTransactionsUseCase()
            .subscribeBy {
                handleResult(it)
            }.addTo(compositeDisposable)
    }

    private fun setLoadState() {
        showProgress.postValue(true)
        showError.postValue(false)
        showContent.postValue(false)
    }

    private fun handleResult(result: Result<List<Transaction>>) {
        when (result) {
            is Result.Success -> setContentState(result.value)
            is Result.Failure -> setErrorState()
        }
    }

    private fun setContentState(transactionsResult: List<Transaction>) {
        showContent.postValue(true)
        transactions.postValue(transactionsResult)
        showProgress.postValue(false)
        showError.postValue(false)
    }

    private fun setErrorState() {
        showError.postValue(true)
        showProgress.postValue(false)
        showContent.postValue(false)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
```

# “Useless Use Cases”

Many times while writing Use Cases you end up in the following situation:

```kotlin
class SomeUseCaseImpl(
    private val someRepository: SomeRepository
): SomeUseCase {
    override fun invoke() = someRepository.getSomething()
}
```

I know this situation very well and I also know this case is more common than the cases in which the Use Cases are actually doing something!

However, even if they “do nothing” other than just calling a repository method, there are several reasons for still using them.

## First: Consistency

Wouldn’t it be bad if some ViewModels would call use-cases while others would directly call repositories?  

Some less experienced developers or new joiners will have a hard time understanding what they are supposed to do while looking at the codebase.

## Second: They protect the code from future changes

One of the purposes of Clean Architecture is to `give you a codebase that can easily adapt when requirements change`, which also means that the amount of code that needs to change should always be minimum.

```kotlin
interface SomeRepository {
    fun doSomething()
}
class SomeViewModel1(private val someRepository: SomeRepository) {
    //...
    fun doSomething() {
        //...
        someRepository.doSomething()
        //...
    }
    //...
}
class SomeViewModel2(private val someRepository: SomeRepository) {
    //...
    fun doSomething() {
        //...
        someRepository.doSomething()
        //...
    }
    //...
}
class SomeViewModel3(private val someRepository: SomeRepository) {
    //...
    fun doSomething() {
        //...
        someRepository.doSomething()
        //...
    }
    //...
}
```

## Third, The “Screaming Architecture”

> What is your app doing?

I guess you have no documentation that explains what your app does.

Don’t get me wrong, I’m not expecting you to have one, however, if I want to know what your app does where should I look at?

The answer is *the code, the code is the only source of truth.*

When you open your project can you tell what your app does?

In most of the projects that you’ll see you won’t be able to understand what the app is about at first look.

# Summary

Let’s now review the initial questions and let’s give them an answer.

> Q: What are these Use Cases/interactors supposed to do?

***A: They implement the dataflow logic.***

> Q: Why can’t I call directly the repository from the Presenter/ViewModel?

***A: Because we want to avoid God objects that deals with both presentation logic and dataflow logic. We also want the dataflow logic to be reusable across different ViewModels.***

> Q: Why should I have a Use Case that does nothing other than just calling a Repository, isn’t this an overkill for my app?

***A: Because of consistency, protection from future changes and for having a “Screaming Architecture”***

> Q: Do I really need a Use Case for each repository method?

***A: Not always, but for sure you need at least a repository method for each Use Case.***

Refrence:

https://proandroiddev.com/why-you-need-use-cases-interactors-142e8a6fe576
