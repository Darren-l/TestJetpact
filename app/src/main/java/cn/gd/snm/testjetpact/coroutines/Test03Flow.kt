package cn.gd.snm.testjetpact.coroutines

import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
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
class Test03Flow(var lifecycleScope: CoroutineScope) : ViewModel() {

    companion object {
        var TAG = this.javaClass.simpleName + "##"
    }


    fun main() {
//        testMutableFlow()
    }

    var mutableLd = MutableLiveData<String>()
    var mutableLd2 = MutableLiveData<String>()
    var mediatorLd = MediatorLiveData<String>()
    /**
     * 用于监听一个或多个liveData的变化。
     *
     */
    fun testMediatorLiveData(owner:LifecycleOwner) {
        mutableLd.observe(owner, Observer<String> {
            Log.d(TAG,"mutableLd obser=${it}")
        })

        mutableLd2.observe(owner, Observer<String> {
            Log.d(TAG,"mutableLd2 obser=${it}")
        })

        mediatorLd.addSource(mutableLd, object:Observer<String>{
            override fun onChanged(it: String?) {
                Log.d(TAG,"mediatorLd addSource it=${it}")
            }
        })

        mediatorLd.addSource(mutableLd2, object:Observer<String>{
            override fun onChanged(it: String?) {
                Log.d(TAG,"mediatorLd addSource it=${it}")
            }
        })
        //todo 一定要加入监听。
        mediatorLd.observe(owner, Observer<String>{
            Log.d(TAG,"mediatorLd observe it=${it}")
        })
    }

    /**
     *  MutableStateFlow可变的flow
     *
     *
     *
     */
    private fun testMutableFlow() {
        GlobalScope.launch{
            val flowA = MutableStateFlow(1)
            val flowB = MutableStateFlow(2)

            val flowC = flowA.combine(flowB) {
                    a, b -> a + b
            }

            launch {
                flowC.collect {
                    Log.v("ttaylor","c=$it")
                }
            }

            launch {
                delay(2000)
                flowA.emit(2)
                delay(20)
                flowB.emit(12)
            }
        }
    }


}