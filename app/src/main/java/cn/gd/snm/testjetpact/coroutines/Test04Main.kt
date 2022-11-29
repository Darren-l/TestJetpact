package cn.gd.snm.testjetpact.coroutines

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.*

/**
 * 测试讲解协程。
 *
 * 1. 开启协程，取消协程的方式。
 * 2. 将函数转化为挂起函数，实现异步的同步化。
 * 3. 协程与子协程的异常捕获。
 *
 * 总结：
 * 使用CoroutineScope的launch和async来启动协程：
 *      launch 适合启动外部不需要返回值的协程
 *      async 适合启动外部需要返回值的协程
 *
 * 对于线程上的优先级，可以在CoroutineContext中设置Dispatcher来让协程运行在指定协程调度器（线程）。
 * 可通过withContext来切换协程内代码块所运行的协程调度器（线程）。
 * 在Android中可以使用拓展库lifecycleScope和viewModelScope作用域管理协程。
 *
 * 关于子协程与父协程取消关联。
 *  1. 作用域（父级协程）取消（异常）时，会取消所有子协程。
 *  2. 作用域取消后无法创建新协程
 *  3. 父级协程需等待所有子协程执行完才能完成，注意这里的完成仅仅只是的是完成的状态值。
 *  4. 默认情况下，子协程未捕获的异常会传递到父协程，如果需要子协程自行处理，则需要使用指定的子类，可以在CoroutineContext
 *  中设置SupervisorJob，或者使用supervisorScope创建子协程作用域，将异常拦截在协程体内部。。
 *
 * 关于协程的取消状态：
 *  1. 调用了job的cancel不代表协程就能立刻被取消。内部可以使用isActive、yield检查协程状态，使其能够被取消。
 *  2. 对于回调API转化的协程，最好使用suspendCancellableCoroutine来创建可取消的协程（内部会检查协程状态）。
 *
 * 关于协程的拦截器：
 *  1. 可以给CoroutineContext设置CoroutineExceptionHandler来作为最后的异常拦截器，处理一些出现异常后的资源回收操作。
 *
 * 关于android封装的scope：
 *  1. viewModelScope 和 LifecycleOwner的lifecycleScope，都会在各自生命周期结束时取消作用域内的协程
 *
 *
 */
class Test04Main(var lifeCoroutineScope: CoroutineScope) {

    companion object{

        val TAG = this::class.simpleName + "D##"
    }

    fun go(){
        //todo 原始开启协程方式
//        test0401()

        //todo 常用创建协程作用域并开启协程方式，job的作用。
//        test0402()

        //todo asnc异步执行及控制异步等待方式。
//        test0403()

        //todo 将函数转化为挂起函数，可以直接将异步的操作，同步化，不需要写回调了。
//        test0404()

        //todo 探究协程CoroutineContext
//        test0405()

        //todo 父协程与子协程的关系。
        test0506()
    }

    /**
     * 在协程作用域中，可以使用launch开启一个子协程作用域。此时子协程的job和CoroutineContext都是新的。
     *
     * 父协程只能在全部子协程执行完成后才会进入完成状态，注意这里是状态值。
     *
     * 1. 子协程作用域的异常会自动抛到父协程作用域中，默认的方式开启自协程是没办法让子协程捕获自己的异常的，如果需要子协程
     * 自行处理异常，则可以使用SupervisorJob或者supervisorScope来开启子协程。
     *
     * 2. 父协程作用域被取消，子协程也会被取消。
     *
     */
    private fun test0506() {
        var job = Job()
        var coroutine = CoroutineScope(job)
        coroutine.launch {
            launch {
                delay(1000)
                Log.d(TAG,"launch1....")
            }

            Log.d(TAG,"launch1....end")

            launch {
                delay(500)
                Log.d(TAG,"launch2....")
            }

            Log.d(TAG,"launch2....end")
        }
    }

    /**
     * 协程中几个重要的对象。
     *  Job：控制协程的生命周期。
     *  CoroutineDispatcher：将工作分派到适当的线程。
     *  CoroutineName：协程的名称，可用于调试。
     *  CoroutineExceptionHandler：处理未捕获的异常。
     *
     */
    private fun test0405() {
        var job = Job()
        var coroutineScope = CoroutineScope(job)

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("handler cast $throwable")
        }

        //todo 为当前协程增加异常捕获器。
        coroutineScope.launch(handler) {

        }
    }


    /**
     * 请求结果。
     *
     */
    interface CallBack{
        fun success(result:String)

        fun fail(error:String)
    }

    /**
     * 封装请求。
     *
     */
    fun getTestData(callback:CallBack){
        //todo  .... 假设这里是网络请求
        thread {
            //todo 成功后，开始回调。
            callback.success("url")
        }
    }


    /**
     * 将指定函数变为suspend函数，执行当前函数必须要在协程作用域中。
     *
     * 接下来getTestData相当于是一个suspend函数，如果当前函数执行完毕后，调用resume，协程才会继续向后执行。
     *
     * 可以把resume看做是当前协程执行结束的通用回调。
     *
     * retrofit自2.6.0版本后就默认支持协程，实际实现就是将原本的异步请求函数，改造成了挂起函数，当请求结束后，调用resume
     * 通知当前协程作用域执行结束。
     *
     */
    suspend fun getData1() = suspendCoroutine<String> { continuation ->
        getTestData(object:CallBack{
            override fun success(result: String) {
                //内部会判断该代码作用域是否执行完毕，若执行后没调用resume，则会将当前协程挂起。
                Log.d(TAG,"suspendCoroutine before...")
                Thread.sleep(5 * 1000L)
                continuation.resume("suspendCoroutine...1111")
                Log.d(TAG,"suspendCoroutine end...")
            }
            override fun fail(error: String) {
                continuation.resumeWithException(Exception(error))
            }
        })
    }

    /**
     * 相比suspendCoroutine，该函数带取消功能。
     *
     *
     */
    suspend fun getData2(value : String) = suspendCancellableCoroutine<String> { continuation ->
        getTestData(object:CallBack{
            override fun success(result: String) {
                Log.d(TAG,"suspendCancellableCoroutine2222 before...")
                continuation.resume("suspendCancellableCoroutine2222... ## $value") {
                    Log.d(TAG, "throwable=${it.printStackTrace()}")
                }
                Log.d(TAG,"suspendCancellableCoroutine2222 end...")
            }
            override fun fail(error: String) {
                continuation.resumeWithException(Exception(error))
            }

        })
    }
    /**
     * 将指定函数变为挂起函数。
     *
     */
    private fun test0404() {
        //todo 原始的线程方式，通常网络请求成功后，会调用自定义的callback进行回调。
//        getTestData(object:CallBack{
//            override fun success(result: String) {
//                //通常还会使用handler进行主线程的切换。
//                Handler().post(Runnable {  })
//            }
//
//            override fun fail(error: String) {
//
//            }
//
//        })

        //todo 测试协程方式。
        /**
         * 开启一个协程作用域后，如果当前的函数为suspend，那么会等待当前挂起函数执行完后，才会继续向后执行。相当于
         * getData1()、getData2()、及后面的打印是线性执行的。
         *
         * 原理在于在同一个协程作用域下，上一个挂起函数没有调用continuation.resume前，协程会一直挂起阻塞。
         *
         */
//        runBlocking {
//            val result = getData1()
//            val result2 = getData2("darren")
//            Log.d(TAG,"result=$result, result2=$result2")
//        }

        // todo 测试正常开启一个协程后，调用挂起函数，效果与测试的runBlocking一致。
        var job = Job()
        var coroutineScope = CoroutineScope(job)
        coroutineScope.launch {
            val result = getData1()
            val result2 = getData2("darren")
            Log.d(TAG,"result=$result, result2=$result2")
        }
    }

    /**
     * async只能作用在协程作用域中，代表作用域中的代码将异步去执行，返回值可控制当前协程作用域是否等待。
     *
     */
    private fun test0403() = runBlocking {

        //todo async作用域launch相似，一个是用于线程的挂起执行，另一个是用于协程中的挂起执行。
        val task1 = async {
            delay(100)
            Log.d(TAG,"task1...")
        }

        val task2 = async {
            delay(200)
            Log.d(TAG,"task2...")
        }

        //todo 和线程类似，可以控制task可以控制协程的等待。
        Log.d(TAG,"await1...")
        task1.await()
        Log.d(TAG,"await2...")
        task2.await()

        Log.d(TAG,"end...")
    }


    /**
     * 创建协程作用域CoroutineScope，该作用域下所有协程都将被追踪，传入的Job可控制作用域内的协程。
     *
//     * launch返回的是协程的句柄，作为管理协程开启和关闭的管理者。
     *
     */
    private fun test0402() {
        Log.d(TAG,"test0402...")

        //todo job的作用：控制协程的执行。
        var job = Job()

        val scope = CoroutineScope(job)   //todo 创建协程作用域

        //todo 挂起执行。
        scope.launch {
            delay(500)
            Log.d(TAG,"test0402... launch1")
        }

        //todo 挂起执行,与上一个挂起互不干涉。
        scope.launch {
            delay(200)
            Log.d(TAG,"test0402... launch2")
        }

        //todo launch会返回job，仅用于控制该作用域下的协程。
        // 注意该job并非创建CoroutineScope传进去的job，该job仅能控制当前launch的作用域。
        var job2 = scope.launch {
            delay(200)
            Log.d(TAG,"test0402... launch3")
        }

        Log.d(TAG,"test0402... cancle")
//        job.cancel()  //控制全部
        job2.cancel()   //仅控制job2对应的launch作用域。
    }

    private fun test0401() {
        Log.d(TAG,"test0401...")
        //启动指定的协程
        coroutine.resumeWith(Result.success(Unit))
    }

    //todo 创建一个协程
    val coroutine = suspend {
        //申明一个挂起函数，并将其传递给一个协程作为代码块执行。
        Log.d(TAG,"suspend...")
        "123"       //todo  最后给协程对象传值。
    }.createCoroutine(object:Continuation<String>{
        override val context: CoroutineContext
            get() = Job()

        override fun resumeWith(result: Result<String>) {
            Log.d(TAG,"resumeWith... ${result.getOrNull()}")    //todo 获取挂起作用域中的传值。
        }

    })

}