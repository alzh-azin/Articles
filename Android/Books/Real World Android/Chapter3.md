# Chapter 3: Domain Layer

### What is a domain layer?

The domain layer is the central layer of your app. It includes the code that describes
your domain space along with the logic that manipulates it.

- **entities:** Objects that model your domain space.

-  **value objects:** Another kind of object that models your domain space.

- **interactors/use cases:** Logic to handle entities and/or value objects and produce a result.

-  **repository interfaces:** Define contracts for data source access.

This layer encompasses the business logic of the app. Your business logic is one of
the most important parts of your app, as it defines how the app works. The less you
mess with it, the better! That’s why the domain layer shouldn’t depend on other
layers.
For example, imagine you change your data layer by migrating from REST to
GraphQL. Or you change your presentation layer by migrating the UI to Jetpack
Compose. None of those changes have anything to do with the business logic. As
such, they shouldn’t affect the domain layer at all.

### Do you really need a domain layer?

-  Keeping your code clean and easy to maintain by focusing the business logic in
  one layer only. Single responsibility code is easier to manage.

- Defining boundaries between code that implements app logic and code that has
  nothing to do with that logic, like UI or framework code. Given how fast the
  Android framework changes, this separation is critical.

- Easing the onboarding of future developers, who can study the layer to understand
  how the app works.

### Entities & value objects

-  Entities have an ID that allows you to tell them apart. Their properties can
  change, but the ID always remains the same.

- Value objects describe some aspect of an entity. They don’t have IDs, and if you
  change one of their properties, you create a new value object. For this reason, they
  should always be immutable.

### What should you model?

Frequently, apps are built to support a pre-existing business. In these cases, the
domain model already exists somewhere — typically in the back end. Therefore,
reproducing the back end’s domain model is usually enough.
