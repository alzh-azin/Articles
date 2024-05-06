# Sealed classes and interfaces

### Sealed classes:

A sealed class or interface is class that can have subclasses, but all of those subclasses must be declared as nested classes within that sealed class. This means that you cannot declare a subclass outside of the sealed class. This restriction allows kotlin compiler to know all the subclasses at compile time, enabling powerful features like `exhaustive when`

### Advantages:

- `Algebraic data type`: Sealed classes are a way to implement Algebraic data type in kotlin which a data type consist of a fix set of possible values. This is useful in functional programming and pattern matching.

- `Exhaustive when statement`: Since the compiler knows about all possible subclasses of this sealed class, it helps catch potential bugs at compile-time and increase performance.

- `Pattern matching`

- `Better performance`: By knowing all possible subclasses at compile-time, the kotlin compiler can perform optimizations and generate more efficient code.

### Usecases:

- State management in UI
  
  ```kotlin
  sealed class UIState {
      data object Loading : UIState()
      data class Success(val data: String) : UIState()
      data class Error(val exception: Exception) : UIState()
  }
  
  fun updateUI(state: UIState) {
      when (state) {
          is UIState.Loading -> showLoadingIndicator()
          is UIState.Success -> showData(state.data)
          is UIState.Error -> showError(state.exception)
      }
  }
  ```

- Payment method handling
  
  ```kotlin
  sealed class Payment {
      data class CreditCard(val number: String, val expiryDate: String) : Payment()
      data class PayPal(val email: String) : Payment()
      data object Cash : Payment()
  }
  
  fun processPayment(payment: Payment) {
      when (payment) {
          is Payment.CreditCard -> processCreditCardPayment(payment.number, payment.expiryDate)
          is Payment.PayPal -> processPayPalPayment(payment.email)
          is Payment.Cash -> processCashPayment()
      }
  }
  ```

- Api response handling
  
  ```kotlin
  sealed class ApiResponse {
      data class UserSuccess(val user: UserData) : ApiResponse()
      data object UserNotFound : ApiResponse()
      data class Error(val message: String) : ApiResponse()
  }
  ```


