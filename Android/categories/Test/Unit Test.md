# Unit Test

- In Android, we use JUnit library for testing

- Assertion, is a code statement that checks whether your code behaves as expected. For example, assertEquals function has two parameters.

- In an application, there are 3 source sets and 2 of them are test source sets. AndroidTest is an instrumental test which contains android framework test. Another test source set is a local test which contains unit tests and does not need an emulator or any device for running.

- **The tests you write could act like a documentation for your code, you should try to write them human readable.**

One of the strategies you could follow to write your tests is **given, when, then** 

**Given:** Setup all you need for your tests. including objects and states

**When:**  Do actions for your specific scenario you want to test.

**Then:** This is where you actually check what happens if you do the scenario in the "when" part and check if your tests passed of failed. This part is a number of assert functions.

This is a suitable naming convention:

> subjectUnderTest_actionOrInput_resultState

for example : getActiveAndCompletedStats_noCompleted_returnsHundredZero

**What is AndroidX Test?** AndroidX is a collection of libraries including Applications and Activities. For example, with this line of code, you can access to application context

```kotlin
ApplicationProvider.getApplicationContext()
```

when you want to test LiveData, it is recommended to follow two steps:

1. Use **InstantTaskExcecutorRule**

2. Ensure **LiveData** observation
- Each peace of your code must be divided up into different methods and classes to allows you to test each section in isolation. Following specific architecture allows you to write tests easily.

There are some reasons for why testing repository is hard:

1.  You need to deal with thinking about creating and managing a database to execute even the simplest test for the repository.

2. Some part of the code might take a long time because of network calling or even fails occasionally because of network error.  You might unintentionally create long running, flaky tests.

3. Sometimes your code might test another functionality that is not in the repository. It means maybe you are testing a functionality that is in the local datasource or is a network calling response and maybe the reason of failing the tests is because there are some issues in the local datasource or remote datasource.

> Flaky tests are tests when you run repeatedly on a specific code, sometimes pass and sometimes fail. 







































-----------------

**My issues**

- What is instantExecutorRule and why should we use it?

- What is Robolectric?




