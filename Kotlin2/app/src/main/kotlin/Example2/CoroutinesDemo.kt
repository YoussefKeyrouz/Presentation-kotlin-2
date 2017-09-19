package Example2

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlin.coroutines.experimental.buildSequence
import kotlin.system.measureTimeMillis

/**
 * Created by Y on 9/17/2017.
 */


/**
 * DEMO INDEX:
 *
 *
 */


//First Coroutine

fun testCoroutine1() {

    var s = "no one"

    launch(CommonPool) { // create new coroutine in common thread pool
        delay(1000) // non-blocking delay for 1 second (default time unit is ms)
        println(s) // print after delay
    }
    println("Hello") // main function continues while coroutine is delayed
    s = "World!"
}


// 1) try removing the delay
// 2) try increasing the delay and do other actions meanwhile
// 3) try using delay outside the coroutine






//runBlocking will allow us to use Suspend in all the function. Like a coroutine wrapper.
// Very useful in unit tests.

fun testCoroutine2() = runBlocking {

    launch(CommonPool) { doWorld() }

    println("Hello,") // main function continues while coroutine is delayed

    println("I'm done")
}

// this is a suspending function
suspend fun doWorld() {
    delay(1000)
    println("World!")
}


// 1) How can we make the "I'm done" wait for the coroutine to finish? Hint: By using join() on the job











//Coroutines are very lightweight

fun testCoroutine3() = runBlocking {
    var num = 0
    val jobs = List(10_000) { // create a lot of coroutines and list their jobs.
        launch(CommonPool) {
            println(num++)
        }
    }

    for(job in jobs) {
        job.join()
    }
    println("FINAL RESULT $num")
}

// 1) notice what would happen if you do not launch it as a coroutine. Note the Button UI. (using delay wil lreport back to the UI)
// 2) try it with 100_000 instead
// 3) try it with threads instead
// 4) Notice the race condition
// 5) try using AtomicInteger()










//Repeat
fun testCoroutine4() = runBlocking {
    launch(CommonPool) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
}










//Canceling a job
fun testCoroutine5() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(CommonPool) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 10) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
}

// Canceling is cooperative. Both sides should abide to it. Use isActive
// Can be canceled during a delay
// throws CancellationException when canceled. Can be handled with try/finally
// Canceling the parent coroutine, will cancel all its children




//NonCancelable. After canceling all suspend jobs will throw CancellableExceptions

fun testCoroutine6() = runBlocking {
    val job = launch(CommonPool) {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            run(NonCancellable) {
                println("Cleaning up stuff")
                delay(1000L)
                println("And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
}









//withTimeout will wrap the coroutine.
fun testCoroutine7() = runBlocking {
    withTimeout(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
}









// Sequential

suspend fun doSomethingUsefulOne(): Int {
    println("Doing 1")
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    println("Doing 2")
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}


fun testCoroutine8() = runBlocking {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("Printing")
        println("The answer is ${one + two}")
    }
    println("Completed in $time ms")
}

// Code in a coroutine is sequencial by default





//Using async
fun testCoroutine9() = runBlocking {
    val time = measureTimeMillis {
        val one = async(CommonPool) { doSomethingUsefulOne() }
        val two = async(CommonPool) { doSomethingUsefulTwo() }
        println("Printing")
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

// async returns a "Deferred" instead of a Job (returned by launch)
// "Deferred" is like a job but a future job. Like a promise.
// In our example. It will execute directly.
// Use await() on a deffered value to get its result.
// Note that measureTimeMillis does not imply a coroutine

// 1) try using CoroutineStart.LAZY in the async() call
// 2) to discover more CoroutineStart options, go back to testCoroutine1 and modify it to use UNDISPATCHED






//Deferred can be declared outside a coroutine (unlike suspend functions and jobs). But it requires a suspend/blocking to use await()
// The result type of asyncSomethingUsefulOne is Deferred<Int>
fun asyncSomethingUsefulOne() = async(CommonPool) {
    doSomethingUsefulOne()
}

// The result type of asyncSomethingUsefulTwo is Deferred<Int>
fun asyncSomethingUsefulTwo() = async(CommonPool)  {
    doSomethingUsefulTwo()
}

fun testCoroutine10() {
    val time = measureTimeMillis {
        // we can initiate async actions outside of a coroutine
        val one = asyncSomethingUsefulOne()
        val two = asyncSomethingUsefulTwo()
        // but waiting for a result must involve either suspending or blocking.
        // here we use `runBlocking { ... }` to block the main thread while waiting for the result
        runBlocking {
            println("Printing")
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}

// 1) What happens if you add a 1000 delay before "Printing"












//Context dispatchers
fun testContextDispatcher()  = runBlocking {

    launch(Unconfined) { // not confined -- will work with main thread
        println("'Unconfined': I'm working in thread ${Thread.currentThread().name}")
    }
    launch(coroutineContext) { // context of the parent, runBlocking coroutine
        println("'coroutineContext': I'm working in thread ${Thread.currentThread().name}")
    }
    launch(CommonPool) { // will get dispatched to ForkJoinPool.commonPool (or equivalent)
        println("'CommonPool': I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
        println("'newSTC': I'm working in thread ${Thread.currentThread().name}")
    }
    launch(UI) { // will get its own new thread
        println("'UI': I'm working in thread ${Thread.currentThread().name}")
    }

}

// What will happen if you call .join() on the UI?
// Can combine context by using the + sign (Non Cancelable).
// If one of the context is canceled, the coroutine is canceled. Can create an empty job to control the cancellation     explicitly.
// Not pat of this scope, but quick





// You can easily jump between context by wrapping coroutines
fun jumpBetweenContexts() {
     val ctx1 = newSingleThreadContext("Ctx1")
     val ctx2 = newSingleThreadContext("Ctx2")
     runBlocking(ctx1) {
        println("Started in ctx1")
        run(ctx2) {
            println("Working in ctx2")
        }
         println("Back to ctx1")
     }
}


// 1) What would happen if we use launch instead of run?











//yielding
val counter = buildSequence {
    var a = 0
    while(true) {
        println("I'm yielding")
        yield(a)
        a++
    }
}

fun testCoroutine11() = runBlocking {
    val numbers = counter.take(8)
    for (num in numbers) {
        println(num)
    }
}

// Note: using elementAt will still loop through the previous ones
// Lazily yielded











//Channels

fun testCoroutine12() = runBlocking {
    val channel = Channel<Int>()
    launch(CommonPool) {
        for (x in 1..15) {
            val res = x * x
            channel.send(res)
        }
    }
    // here we print five received integers:
    repeat(5) {
        println(channel.receive())
        delay(1000)
    }
    println("Done!")
}



// 1) Try adding println("Sending...$res") before sending
// 2) Try increasing the repeat number to 10. What would happen?
// 2) Try commenting out the receive()
// 3) Try using buffered channels

// Channels are FIFO












fun testCoroutine13() = runBlocking {
    val channel = Channel<Int>()
    launch(CommonPool) {
        for (x in 1..5) channel.send(x * x)
        channel.close() // we're done sending
    }
    // here we print received values using `for` loop (until the channel is closed)
    for (y in channel) {
        println(y)
    }

    println("Done!")
}

// 1) what would happen if you try receiving after the channel is closed? Spoiler Alert: It will crash. Hint: Use onReceiveOrNull













//Producers are wrappers around channels
fun produceSquares() = produce(CommonPool) {
    for (x in 1..5) {
        val res = x * x
        println("Producing...$res")
        send(x * x)
    }
}

fun testCoroutine14() = runBlocking {
    val squares = produceSquares()
    squares.consumeEach {
        println(it)
    }

    println("Done!")
}



// Multiple coroutines can access the same channel, to send/receive. Great for dividing the work.



//Go back to the slide