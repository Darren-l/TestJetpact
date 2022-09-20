package cn.gd.snm.testjetpact.coroutines

import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.concurrent.thread

/**
 * 测试android中的flow。
 *
 *  可以看做是简版的RxJava
 *
 * 冷流和热流区别：
 *  冷流：在没有切换线程的情况下，上游和下游处于同一线程，同步非阻塞状态，也就是一一执行上下游事件。
 *  热流：上游和下游处于异步非阻塞状态，也就是上游发送和下游处理并不同步，
 *
 */
class Test03(var lifecycleScope: CoroutineScope) : ViewModel() {

    companion object {

        var TAG = this.javaClass.simpleName + "##"

    }


    fun main() {

        test0301()

    }


    private fun test23(coroutineScope: CoroutineScope) {
        val flowA = MutableStateFlow(1)
        val flowB = MutableStateFlow(2)
        val flowC = flowA.combine(flowB) { a, b -> a + b }
        coroutineScope.launch {
            Log.d(TAG,"1111111")
            flowC.collect {
                Log.v(TAG,"onNext=$it")
            }
        }
        coroutineScope.launch {
            Log.d(TAG,"1211111")
            delay(2000)
            flowA.emit(2)
            flowB.emit(2)
        }
    }

    private fun test0301() {
        Log.d(TAG, "test0301...")

        thread {
            runBlocking {
                test23(this)
            }


        }
    }




}