# The “Real” Clean Architecture in Android: S.O.L.I.D.

# What is Clean Architecture?

> Clean Architecture is the outcome of the S.O.L.I.D. principles.

# S.O.L.I.D.

***They are the benchmark to determine how clean your code is.***

You don’t know if you have the right design? *Check if your code adheres to S.O.L.I.D.* just like a doctor would check your blood test, if something is wrong your blood test will tell, the same is for S.O.L.I.D. and your code.

Now, just because a principle has been violated, it doesn’t mean you are going to have issues but if you have issues, bugs or you are less productive you are most likely breaking one or more S.O.L.I.D. principles.

*Look at S.O.L.I.D. principles as common-sense disciplines that can help you stay out of trouble, you may not have to use them always but* ***you better know them!***

# Liskov Substitution Principle

> Derived classes must be substitutable for their base classes.

Inheritance is the tightest coupling you can have, *bad usage of it leads to highly-coupled and lowly-cohesive code*.

Most inexperienced engineers use inheritance as the primary way to achieve reusability but this often makes maintenance almost impossible to achieve (and just so you know, repeating code is much better than coupling code, maintenance > reusability).

Let’s have a look at this over-simplistic example (which is not the square/rectangle example you’ll see around) where the LSP is violated:

```kotlin
abstract class Employee {

    abstract fun getSalary(): String

    abstract fun getLineManager(): Employee

}

class SeniorAndroidDeveloper(
    private val leadAndroidDeveloper: LeadAndroidDeveloper
) : Employee() {

    override fun getSalary(): String {
        return "$10000"
    }

    override fun getLineManager(): Employee {
        return leadAndroidDeveloper
    }

}

class LeadAndroidDeveloper(
    private val cto: CTO
) : Employee() {

    override fun getSalary(): String {
        return "$10001"
    }

    override fun getLineManager(): Employee {
        return cto
    }

}

class CTO : Employee() {

    override fun getSalary(): String {
        return "$1000000000000000000"
    }

    override fun getLineManager(): Employee {
        throw RuntimeException("A CTO doesn't have a line manager")
    }
}
```

In this scenario, the developer decided to make CTO a subclass of Employee for reusing the `getSalary` method and because of that now the app could crash if the method `getLineManager` is called on a CTO object.

I know for certain that 99% of you wouldn’t have made this mistake because you know that a CTO is a *chief* and not an *employee* but not always relationships are clear in the codebase.  
In fact, I bet that you have seen many projects with massive `BaseActivity`, `BaseFragment` , `BaseViewModel`…. classes, that is another clear example of LSP violation.

> So it is fine to use inheritance as far as I don’t break the LSP?

Instead of using inheritance and then switching to another approach if LSP is violated, ***you should change your way of thinking and default to composition***.

99% of the things you want to do with inheritance can be done with composition so you should always ***favor* *composition over inheritance***.

other references : 

[Composition over Inheritance]([Composition over Inheritance - YouTube](https://www.youtube.com/watch?v=wfMtDGfHWpA&t=33s))

# **S**ingle Responsibility Principle

> A class should do one, and only one, thing.

Hmmm… you meant *“A* ***function*** *should do one, and only one, thing?”.***The wrong principle**, try again.

> A class should have one, and only one, reason to change.

Better, but maybe rephrase it in a way that explains what that *“reason to change”* is…

> *A class should be responsible to one, and only one, actor (group of users or stakeholders).*

*“Cohesion”* is the keyword in the SRP: the methods of the class must be cohesive,` if they are not you should move those methods to another class.`

## From SRP to CCP

At the **component level,** the **SRP** becomes the **Common Closure Principle (CCP)** which can be phrased in the following way:

> *Gather into components those classes that change for the same reasons and at the same times.*

> *Separate into different components those classes that change at different times and for different reasons*.

The **CCP** is one of the most important principles to follow for having a rock-SOLID **modularization** (another topic I’ll cover in the following articles).

![Capture.PNG](..\resources\Capture.PNG)



ccp references:

[The Common Closure Principle](https://ericbackhage.net/clean-code/the-common-closure-principle/#:~:text=The%20Common%20Closure%20Principle%20(CCP,have%20multiple%20reasons%20to%20change.)
