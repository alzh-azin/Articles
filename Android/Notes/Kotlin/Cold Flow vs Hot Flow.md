# Cold Flow vs Hot Flow

cold flow vs hot flow

| Cold Flow                                    | Hot Flow                                      |
| -------------------------------------------- | --------------------------------------------- |
| It emits data only when there is a collector | It emits data only even there is no subsciber |
| It does not store any data.                  | It can store data                             |
| It can't have                                |                                               |

#### Cold Flow

In cold flow, in the case of multiple collectors, the flow will be executed and emit all the values for new collector and do its task.

```kotlin
fun getNumbersColdFlow(): ColdFlow<Int> {
    return someColdflow {
        (1..5).forEach {
            delay(1000)
            emit(it)
        }
    }
}
```

```kotlin
val numbersColdFlow = getNumbersColdFlow()

numbersColdFlow
    .collect {
        println("1st Collector: $it")
    }

delay(2500)

numbersColdFlow
    .collect {
        println("2nd Collector: $it")
    }
```

Output:

```kotlin
1st Collector: 1
1st Collector: 2
1st Collector: 3
1st Collector: 4
1st Collector: 5

2nd Collector: 1
2nd Collector: 2
2nd Collector: 3
2nd Collector: 4
2nd Collector: 5
```

#### Hot Flow

In hot flow, in the case of multiple collectors, the flow will keep on emitting the values and each flow collect the values from where they have started collecting.

```kotlin
fun getNumbersHotFlow(): HotFlow<Int> {
    return someHotflow {
        (1..5).forEach {
            delay(1000)
            emit(it)
        }
    }
}
```

```kotlin
val numbersHotFlow = getNumbersHotFlow()

numbersHotFlow
    .collect {
        println("1st Collector: $it")
    }

delay(2500)

numbersHotFlow
    .collect {
        println("2nd Collector: $it")
    }
```

Also, hot flow can have some configurations to store data, for example, one last data get stored

```kotlin
fun getNumbersHotFlow(): HotFlow<Int> {
    return someHotflow {
        (1..5).forEach {
            delay(1000)
            emit(it)
        }
    }.store(count = 1)
}
```

```kotlin
val numbersHotFlow = getNumbersHotFlow()

numbersHotFlow
    .collect {
        println("1st Collector: $it")
    }

delay(2500)

numbersHotFlow
    .collect {
        println("2nd Collector: $it")
    }
```

Output:

```kotlin
1st Collector: 1
1st Collector: 2
1st Collector: 3
1st Collector: 4
1st Collector: 5

2nd Collector: 2
2nd Collector: 3
2nd Collector: 4
2nd Collector: 5
```
