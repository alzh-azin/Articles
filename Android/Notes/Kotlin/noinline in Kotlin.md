# noinline in Kotlin

When we mark a function with `inline` keyword, all the lambdas in input parameters will be `inline`. If you don't want to marke an input lambda as a `inline` lambda, you can use `noinline` to avoid the inlining.
