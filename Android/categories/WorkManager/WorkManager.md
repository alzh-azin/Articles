# WorkManager

What is WorkManager?

WorkManager is a part of `Android Jetpack` and an architectural component for background task that need a combinations of opportunistic an guaranteed execution.

`Opportunistic execution` means WorkManager does your background work as soon as possible.

`Guaranteed execution` means WorkManager takes care of starting your work in a variant situations, even you navigate away from your app.

Some of WorkManager benefits are:

- Supports for both asynchronous one-off and periodic tasks.

- Support declaring variant constraints such as network conditions, storage space and charging status.

- Chaining of complex work requests, such as running work in parallel.

- Output from one work request to used as another work input.

> WorkManager sits on top of a few APIs, such as `JobScheduler` and `AlarmManager`. It depends on device API level to decide which API to use.

Different WorkManager classes:

- `Worker/CoroutineWorker` Worker is a class that executed on the background thread. This class has a function named `doWork()` and it is a function you put your code to perform in the background.

- `WorkRequest` is a class that you create a request to do some work. Its a place that you define variant constraint and Whether you need a OneTimeWorker or Periodic worker.

- `WorkManager` is a class that schedule your work request and make it run.
