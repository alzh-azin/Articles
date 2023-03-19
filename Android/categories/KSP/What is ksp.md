# What is ksp?

<u><a rel="noopener" href="https://github.com/google/ksp">KSP</a></u> (Kotlin Symbol Processor) is a new API from Google for writing Kotlin compiler plugins. Using KSP we can write annotation processors to reduce boilerplate, solve cross-cutting concerns, and move checks from runtime to compile-time.

While we already have annotation processors in Kotlin through *kapt*, the new KSP API provides promises speed and ergonomics.

Firstly, KSP is faster since *kapt* depends on compiling Kotlin to Java stubs for consumption by the`javax.lang.model` API used by Java annotation processors. Compiling these stubs takes a significant amount of time, and since KSP can skip this step we can write faster processors.


