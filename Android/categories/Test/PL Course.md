# Testing on Android

- Without tests, you need to manually test a functionality over and over again to verify it is working.

- Using JUnit, we can test our functionality in our code with a single click.

- A professional project does not complite without test.

## Testing Pyramid

![test-pyramid.png](/home/azinalizadeh/Desktop/test-pyramid.png)

**Unit Test (70%)**: Tests of single units of our app (e.g. testing a function or a class)

**Integration Test (20%)**: Tests how two specific component of our app work together.

**UI Test (10%)**: Tests if many or all components of app correctly work together and if the UI looks like what it should

## What are good tests?

**Test Driven Development**: the main principle of TDD is about write test cases before main implemantation of that functionality (it works only for unit test)

1. Write the function signature

2. Write the test cases for that function

3. Write the function logic so that the tests pass

> You should only have one assertion per test case, because we immediately want to know what is the reason that our test fails.

but sometimes in some cases, there is no way around multiple assertion but you should keep it as little as possible.

## What makes a good test?

1. **Scope**: the scope determines how much of actual code in our function is covered by a single test case.

2. **Speed:** the speed factor determines how fast our test case runs. The importance of this factor is the faster you test runs, the more often you will run your test and the more bugs you will find.

3. **Fidelity**: this factor means how close our test cases is to a real scenario
- Prevent flaky tasks. Flaky tasks is the tasks that sometimes succeed and sometimes fail)

- Never make the outcome of a test case depends on the outcome of another component or the outcome of another test

## How many test cases should we write?

You should write test cases in a same equivalent class as little as possible, but also as many as possible in different equivalent classes.

For example: we have a registration screen including fields such as username, password and cofirm password. we want to write tests that belong to different equivalent classes and cover different scenarios as much as possible.

Designing good equivalent classes could be like this:

- **Take some registration inputs that leads to successful registration.** for example: usename: Azin, password: 12345 , cofirm password: 12345. this is a good test case that is taken from an equivalent class so that you doesn't need to write another test case that is similar to previous test case. changing username from Azin to X or Y does not count as a different test case that could lead to a different result

- **Leave username field empty and submit the form then it should lead to an error response**

- **Submit a username that is already exists**

- **Submit an incorrect confirm password**

Above examples are good samples for writing test cases with different equivalent classes.

## @Before & @After

All of the test cases should run independent of each other and they shouldn't rely on something common. for example, you shouldn't write test cases that use a common object that is initialized in the class because 

Lets imagine you initialized an object in your test class and in has a variable named `count`. there might be a test case that increase the amount of `count` variable and then when the second test case wants to use this variable, leads to a problem and this test case turns into a flaky test case because in this situation, sometimes your tests might be passed and sometimes be failed.

To resolve that, we can use @Before function to do something that you need before every test case, and also, you can use @After so that you can do something after each test case runs.


