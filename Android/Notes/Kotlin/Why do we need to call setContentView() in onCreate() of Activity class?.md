# Why do we need to call setContentView() in onCreate() of Activity class?

Question: What does setContent function do?

Answer: It defines root view of the activity.

setContent function get called in onCreate() because it get called in the lifecycle of the activity only once.

It is not eficient to call setContent in onResume() or another lifecycle functions because they might get called more than once.
