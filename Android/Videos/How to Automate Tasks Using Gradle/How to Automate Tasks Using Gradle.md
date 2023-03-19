# How to Automate Tasks Using Gradle

```groovy
task helloWorld(){
    println "Hello world"
}
```

you will see here's our hello world string and you'll also notice that the task actually executes in line 2 but hello world is printed in line 1

> Configure project :app
> Hello world

> Task :app :helloWorld UP-TO-DATE

the reason it's printed here is that if we just put the stuff inside of the single task then the code is executed once the task is actually configured.

if we want to control or if you want to execute code once or be like before or after the task is actually executed we can use something like this:

```groovy
task helloWorld(){
    doFirst{
        println "Hello world first"
    }
    println "Hello world"
    doLast{
        println "Hello world last"
    }
}
```

> Configure project :app
> Hello world

> Task :app :helloWorld
> Hello world first
> Hello world last

you will notice now we still have our hello world string printed at the very first which is during the configuration phase however the hello world first and hello world last strings are not executed in that corresponding order when the task is actually executed
