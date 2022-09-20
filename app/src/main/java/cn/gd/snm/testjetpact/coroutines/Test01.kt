package cn.gd.snm.testjetpact.coroutines

import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.concurrent.thread

/**
 * 测试android中的协程，与纯kotlin应用的区别在于，GlobalScope的挂起代码作用域，不需要依托于当前线程。
 *
 *
 */
class Test01 : ViewModel() {

    companion object {

        var TAG = this.javaClass.simpleName + "##"

    }

    /**
     * 1. 测试viewModelScope.launch在主线程开启的协程是否能执行主线程操作。
     * 2. 测试withContext开启的io线程中的协程，是否具备阻塞所依附的协程内后续的逻辑。
     *
     */
    fun main() {

//        Log.d(TAG, "main thread id=${Looper.getMainLooper().thread.id}")

        //todo 测试viewModelScope及withContext
//        testViewModelScope()


        //todo 测试viewModelScope.launcher的阻塞
        testVm2()


    }

    var t2:Thread ?= null
    private fun testVm2() {
        Log.d(TAG, "testVm2...")


        //todo 测试android的viewModelScope,类似kt中的GlobalScope
//        thread {
//            viewModelScope.launch {
//                delay(2000)
//                Log.d(TAG, "launcher... end...")
//            }
//            Log.d(TAG, "testVm2... end...")
//        }

        //todo 测试withContext,类似coroutineScope，不管父协程处于哪个线程，都可以阻塞。
        thread{
            viewModelScope.launch() {
                Log.d(TAG, "launcher...")
                withContext(Dispatchers.IO){
                    Log.d(TAG, "withContext...")
                    delay(3000)
                }
                Log.d(TAG, "launcher... end...")
            }

        }

        //todo 测试kt的协程GlobalScope
        //todo 理论上，协程是需要依托于线程来执行的，线程结束协程也无法执行。但实际android的协程，即使线程已经
        // 结束协程也依然能够继续执行。 ---- 古怪
//        t2 = thread {
//            GlobalScope.launch {
//                delay(4000)
//                println("## GlobalScope.launch...coIsLive=${t2!!.isAlive}")
//            }
//            println("## hello")
//            println("## word2")
//        }


        //todo 测试kt协程作用域中的coroutineScope -- 可以正常阻塞。
//        thread{
//            GlobalScope.launch {
//                Log.d(TAG, "launcher..")
//                coroutineScope {
//                    Log.d(TAG, "coroutineScope1..")
//                    delay(3000)
//                    Log.d(TAG, "coroutineScope1 end..")
//
//                }
//
//                Log.d(TAG, "launcher.. end")
//            }
//
//            Log.d(TAG, "testVm2... end...")
//        }
    }

    private fun testViewModelScope() {
        //todo  在主线程中的协程。
        viewModelScope.launch {
            Log.d(TAG, "test1 thread id=${Thread.currentThread().id}")
        }

        //todo Io线程中的协程
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "test2 thread id=${Thread.currentThread().id}")
        }

        //todo 也可以在协程作用域中去开辟新的协程，该协程可以指定是否在主线程。
        viewModelScope.launch {
            testWithContext()
        }
    }

    private suspend fun testWithContext() {
        //todo 将协程移到一个io线程
        withContext(Dispatchers.IO) {
            Log.d(TAG, "test3 thread id=${Thread.currentThread().id}")
        }
    }

}