# Singleton Classes

Singleton classes are retrieved from singleton design pattern.

**What is the singleton design pattern?** The singleton design pattern, is a software design pattern that restrict the instantiation of a object to only one instance for the entire lifetime of the application. 

In android, when we want to have objects that is required to create once and and get used everywhere, we can use this design pattern. It is used when we want to have an object that the cost of creating it is high and repeatedly creating these objects, uses up system resources. Using this design pattern could improve the performance by avoiding the overhead of creating and destroying multiple instances of a resource. So it is better to create them only once and use it again and again. Classes like Retrofit, Okhttp, database are suitable for this usage.

In kotlin, we need to use keyword `object` to create a singleton class. The `object` class can have variables, functions and init block but constructor method is not allowed in singleton classes. Instead, if we need some initialization after the object is created, we can use init block. The object get instantiated when it is used for the first time.

you can consider the `object` as an ordinary class so that you can use an object to extend some classes or implement some interface.

Another thing you should consider is you need to make sure the object does not get used by multiple threads at the same time and for that, we have to use `synchronize` function to lock the class to prevent accessing it by another threads and classes.

### Properties of Singleton Class

- Only one instance

- Globally accessible

- Constructor not allowed

### Importance of Singleton Objects in Android

- The important use-case of using singleton classes is in situations that we want a single instance of particular object so that we can use it through the life-time of the application. Common use-cases when you use singleton design pattern is when you want to use `Retrofit` class for every single request in your application. In this case, you only need to a single instance of Retrofit class for entire network requests. As you know, every retrofit object contains some properties attached to it  like `Gson Convertor` and `Okhttp` and etc. so if you want to create a new of the instance Retrofit per every network request, it leads to wasting of memory and time.

- In another case, when you are working with repositories in MVVM architecture, you should only create the instance of repository once, because repositories are not going to change and creating different instances would result in space increment and time wastage.

# Companion Objects

In every programming language when we want to access methods or member functions, firstly we create the object and after that, we can access to member classes via the object. But if there are situations that you want to access some special member functions without creating an object only using the class name, you can use companion object.

The important thing you shoud notice is you cannot use class members from companion object and vice versa, you cannot access companion members from accessing its objects.



A companion object, is a special type of singleton class but with this difference that it is associated with the class. It can be used for declaring factory design pattern, static members or extention functions. Companion objects are thread-safe  and lazily initialized it means they are created when they are get used for the first time.

# Accessing Variables

Accessing a variable through a companion object and a singleton class in Kotlin does have some memory-related differences, because they represent different concepts:
