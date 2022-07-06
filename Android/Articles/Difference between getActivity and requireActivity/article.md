# Difference between getActivity and requireActivity

```kotlin
    /**
     * Return the {@link Context} this fragment is currently associated with.
     *
     * @see #requireContext()
     */
    @Nullable
    public Context getContext() {
        return mHost == null ? null : mHost.getContext();
    }

    /**
     * Return the {@link Context} this fragment is currently associated with.
     *
     * @throws IllegalStateException if not currently associated with a context.
     * @see #getContext()
     */
    @NonNull
    public final Context requireContext() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to a context.");
        }
        return context;
    }

    /**
     * Return the {@link FragmentActivity} this fragment is currently associated with.
     * May return {@code null} if the fragment is associated with a {@link Context}
     * instead.
     *
     * @see #requireActivity()
     */
    @Nullable
    final public FragmentActivity getActivity() {
        return mHost == null ? null : (FragmentActivity) mHost.getActivity();
    }

    /**
     * Return the {@link FragmentActivity} this fragment is currently associated with.
     *
     * @throws IllegalStateException if not currently associated with an activity or if associated
     * only with a context.
     * @see #getActivity()
     */
    @NonNull
    public final FragmentActivity requireActivity() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
        }
        return activity;
    }  }
```

**requireContext** and **requireActivity** throw an IllegalStateException if it is null

but **getContext** and **getActivity** return null when that fragment is not attached to the activity

In short, if you want to get the host activity inside fragment’s lifecycle methods, it’s ok to use `requireActivity()` without checking null. If you want to use the activity outside the lifecycle, for example, IO callback, it's better to check null + check destroyed. If you are 200% sure that in your fragment’s lifecycle, activity is not null, use requireActivity() as it needs no **!!** notation inside code, otherwise put it inside the try-catch block to avoid Exception.


