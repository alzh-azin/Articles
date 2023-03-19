# Gradle for Begginers

gradle is a so-called build automator and what that means is it takes all your project configuration that we define in these gradle files and executes different tasks in the right order to put everything together to an executable app

while a common misconception is that gradle actually compiles your project that's not true however for example gradle makes use of the compiler but it also makes use of other tools for example tools that package your resources together that you have in your package so in the end it just takes all these different little programs to put everything together to an apk file that you can actually install and use on your android devices.

### Gradle Wrapper

gradlew file that is a binary file executable and the w stands for wrapper so that's the gradle wrapper and it's kind of a script that makes sure that it installs a specific version of gradle that you specify and then actually run specific tasks.

so everything gradle can really do is considered a task.

if we type 

```bash
./gradlew tasks
```

then it will actually list all the tasks we can currently execute here with gradle

### Build Types

what is a build type? a build type kind of specifies at which stage your app actually is.

for all these different stages you have in your app you can define a build type inside of this block

```groovy
buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
```

also what's very common for the release build is that we use proguard. so proguard or r8 is used to actually shrink your app to optimize your code to obfuscate it to make it harder to reverse engineer

### Product Flavor

a product flavor is similar to a build type so it defines different

versions and different types of your app but the difference to build type is that it actually affects your actual users so while the build type actually more describes the current stage of your application whether it's in alpha beta production or whatever

the product flavors are different types in the sense for example if you have a free and paid version of your app or if specific versions of your app need different minimum sdks then you can define that with so-called product flavors 

```groovy
    flavorDimensions "paidMode" , "minSDK"
    productFlavors{
        free{
            dimension "paidMode"
            applicationIdSuffix ".free"
        }
        paid{
            dimension "paidMode"
            applicationIdSuffix ".paid"
        }

        minSdk30{
            dimension "minSDK"
            minSdk 30
        }

        minSdk21{
            dimension "minSDK"
            minSdk 21
        }
    }
```

you can define or include specific dependencies only for very specific build variants

```groovy
freeMinSdk21DebugImplementation 'org.mockito:mockito-core:3.11.2'
```

### Source sets

By default, Android Studio creates the `main/` source set and directories for everything you want to share between all your build variants. However, you can create new source sets to control exactly which files Gradle compiles and packages for specific build types, product flavors, combinations of product flavors (when using flavor dimensions, and build variants.

For example, you can define basic functionality in the `main/` source set and use product flavor source sets to change the branding of your app for different clients, or include special permissions and logging functionality only for build variants that use the debug build type.
