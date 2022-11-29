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
class Test02Flow(var lifecycleScope: CoroutineScope) : ViewModel() {

    companion object {

        var TAG = this.javaClass.simpleName + "##"

    }


    fun main() {

        //todo 测试 --- 类似于observable
        testFlow()

        //todo 测试过滤操作符filter和流构建方式
        testFilter()

        //todo 切换线程
        testFlowOn()

        //todo 设置超时
        testTimeOut()

        //todo 测试异常
        testException()

        //todo 测试flatMapMerge
        testFlatMapMerge()

        //todo 常用操作符
        testCommon()

        //todo 测试MutableShare
        testMutableShareFlow()
    }


    /**
     * transform： 加强版map，可调用多次发射。
     *
     * take: 限制发射源的范围 -- 和rxjava差不多
     *
     * zip:和rxjava差不多，按自定义的回调规则合并两个发射源的数据为一个数据。
     *
     */
    private fun testCommon(){
        runBlocking {
//            (0..5).asFlow()
//                .transform {
//                    emit(it * 10)
//                    delay(200)
//                    emit(it * 20)
//                    emit("不发了..")
//                }
//                .collect{
//                    Log.d(TAG,"onNext... it=$it")
//                }

            //todo 只去往前面两个发射源数据
//            (0..20).asFlow()
//                .take(2)
//                .collect{
//                    Log.d(TAG,"onNext... it=$it")
//                }


            //todo 按回调规则处理所有发射的数据源，如累加。
//            val sum = (1..3).asFlow()
//                .map { it * it }
//                .reduce { a, b -> a + b }
//            Log.d(TAG,"sum=$sum")

            //todo 类似reduce，不过带一个初始值
//            val sum = (1..3).asFlow()
////                .map { it * it }
//                .fold(3) { a, b -> a + b }
//            Log.d(TAG,"sum=$sum")

            //todo 合并两个发射源，和rxjava差不多
//            val flowA = (1..5).asFlow()
//            val flowB = flowOf("one", "two", "three","four","five").onEach { delay(100) }
//            flowA.zip(flowB) { a, b -> "$a and $b" }
//                .collect {
//                   Log.d(TAG,"onNext=$it")
//                }

            //todo 与zip不同的是，flowA会以flowB的最新的item合并，不会等delay。
//            val flowA = (1..5).asFlow().onEach { delay(100)  }
//            val flowB = flowOf("one", "two", "three","four","five").onEach { delay(200)  }
//            flowA.combine(flowB) { a, b -> "$a and $b" }
//                .collect {
//                    Log.d(TAG,"onNext=$it")
//                }


            //todo 与zip不同，合并的是数据源本身，两个数组变成一个大数组。不会等待delay
            //todo add -- 实际上会等待挂起函数中的delay。
            val flowA = (1..5).asFlow()
            val flowB = flowOf("one", "two", "three","four","five").onEach { delay(100) }

            flowOf(flowA,flowB)
                .flattenConcat()
                .collect{
                    Log.d(TAG,"onNext=$it")
                }

        }

    }

    /**
     * 相当于RxJava中的flatMap操作符
     *
     *
     */
    private fun testFlatMapMerge() {
        runBlocking {

            //todo 处理上游数据，并插入新的事件 -- concat是有序，Merge是无序。
            (0..10).asFlow()
                .flatMapConcat {
                    flow {
                        emit(it)
                    }
                        .flowOn(Dispatchers.IO)
                }
                .collect{
                    Log.d(TAG,"onNext=$it")
                }
        }

    }

    /**
     * catch操作符可以捕获上游异常，对于下游的异常如collect中的，建议直接使用tryCatch。
     *
     */
    private fun testException() {
        runBlocking {

            flow {
                emit(1)
                throw Exception("发送错误...")
            }.catch{
                Log.d(TAG,"捕获错误..")
            }.onCompletion {
                Log.d(TAG,"onCompletion...")
                //todo 如果it不为空，则代表出现错误，若错误被捕获，也同样为空。

                if(it != null){
                    Log.d(TAG,"出现错误，但不知道错误类型...")
                }else{
                    Log.d(TAG,"没有出现错误..")
                }
                Log.d(TAG,"onCompletion... end..")
            }.collect{
                Log.d(TAG,"onNext it=$it")
            }

        }
    }


    /**
     * 设置超时
     *
     */
    private fun testTimeOut() {
        runBlocking {
            withTimeoutOrNull(2500) {
                flow {
                    for (i in 0..5) {
                        delay(1000)
                        emit(i)
                    }
                }.collect {
                    Log.d(TAG, "onNext=${it}")
                }

                Log.d(TAG, "time is up")
            }
        }
    }

        /**
         * 和rxjava一样，flow也支持切换上下文
         *
         * flowOn可指定上游（发射端，包括其onXX的回调）的线程,上游的处理可以多次切换线程，如map
         *
         * 下游的collect处于当前协程作用域所在的线程。
         *
         */
        private fun testFlowOn() {
            Log.d(TAG, "main thread id= ${Looper.getMainLooper().thread.id}")

            runBlocking {
                flow {
                    Log.d(TAG, "emit thread id= ${Thread.currentThread().id}")
                    emit(1)
                }.flowOn(Dispatchers.IO)
                    .collect {
                        Log.d(TAG, "onNext thread id= ${Thread.currentThread().id}")
                    }

            }

        }

        /**
         * filter可以设置过滤，对满足条件的多个数据源进行判断，为true才发射。
         *
         * rxjava中可以使用just等操作符建立多个数据源的流，flow也有类似的接口 -- flowOf
         *
         */
        private fun testFilter() {
            runBlocking {
                //todo 构建流方式一，自动发射多个  -- 冷流
            flowOf(1,2,3,4)
                .collect{
                    Log.d(TAG,"onNext=${it}")
                }
//
//            //todo 方式二，自动发射多个   -- 冷流
//            (0..5).asFlow()
//                .filter {
//                    it%2 == 0
//                }.map {
//                    "covert string:${it}"
//                }.collect{
//                    Log.d(TAG,"onNext=${it}")
//                }
//
//            //todo 方式三 手动发射多个 -- 冷流
//            flow{
//                emit(1)
//                emit(2)
//            }.collect{
//                Log.d(TAG,"onNext=${it}")
//            }

                //todo 方式四 手动发射多个 -- 热流,上游和下游非同步
                channelFlow {
                    for (i in 0..5) {
                        delay(100)
                        Log.d(TAG, "sended...i=$i")
                        send(i)
                    }
                }.collect {
                    delay(200)
                    Log.d(TAG, "onNext=$it")
                }
            }

        }


        /**
         * Flow是冷流，只有订阅者开始订阅后，才会发射数据。
         *
         *
         */
        private fun testFlow() {
            runBlocking {
                flow<Int> {
                    emit(1)
                    emit(2)
                    emit(3)
                }.onStart {
                    Log.d(TAG, "onStart...")
                    //todo 流开始，开始订阅回调
                }.onEach {
                    Log.d(TAG, "onEach...it=${it}")
                    //todo 如有多个数据，则每个数据源发送前先会走这里，注意是数据源并非map后。
                }.onCompletion {
                    Log.d(TAG, "onCompletion...")
                    //todo 当前流结束

                }.onEmpty {
                    Log.d(TAG, "onEmpty...")

                }.catch {
                    Log.d(TAG, "catch 111...")
                    //todo 上一个On出现异常，则这里捕获，可以多个catch

                }.map<Int, String> {
                    //todo  这里通常是数据的转化
                    Log.d(TAG, "onMap")
                    "coverted = $it"
                }.collect {
                    // todo 具体的订阅者的订阅事件
                    Log.d(TAG, "onNext it=$it")
                }

            }
        }


        /**
         * MutableShareFlow特点：
         *
         *  1. 热流，没有接受者也会发射数据。
         *  2. 是stateFlow泛化数据流。
         *  3. 可以有多个数据接收者。
         *
         * 构造方法参数含义：
         *  replay 代表粘性发送的数据个数。
         *  extraBufferCapacity 额外的数据缓存池容量，默认为0对应Int.MAX_VALUE
         *  onBufferOverflow：超过缓存策略
         *      BufferOverflow.SUSPEND ： 超过就挂起，默认实现
         *      BufferOverflow.DROP_OLDEST : 丢弃最老的数据
         *      BufferOverflow.DROP_LATEST : 丢弃最新的数据
         *
         * 使用场景：
         *  1. 带粘性。
         *  2. 可自定义缓存。
         *  3. 多个订阅者。
         *
         */
        private fun testMutableShareFlow() {
            Log.d(TAG, "testMutableShareFlow...")

            val mutableSharedFlow = MutableSharedFlow<String>()

            //todo 热流 -- 收藏必须写在发射之前，默认是没有粘性的。
        lifecycleScope.launch {
            //todo 添加第一个观察者
            Log.d(TAG,"collect...")
            mutableSharedFlow.collect{
                Log.d(TAG,"collect it=${it}")
            }

            //todo collect是挂起函数，会不断地挂起恢复监听。
//            mutableStateFlow.collect{
//                Log.d(TAG,"collect it2=${it}")
//            }
        }

        //todo 重新定义一个协程作用域，解决挂起函数阻塞问题
        lifecycleScope.launch {
            mutableSharedFlow.collect{
                Log.d(TAG,"collect it2=${it}")
            }
        }

        lifecycleScope.launch {
            Log.d(TAG,"emit...")
            mutableSharedFlow.emit("first")
            mutableSharedFlow.emit("sec")
            mutableSharedFlow.emit("thr")
        }


            //todo 定义带粘性的flow -- replay代表重新发送的事件个数，2代表发送最新的两个数据
            var mutableSharedFlow2 = MutableSharedFlow<String>(replay = 2)
            lifecycleScope.launch {
                Log.d(TAG, "emit...")
                mutableSharedFlow2.emit("first")
                mutableSharedFlow2.emit("sec")
                mutableSharedFlow2.emit("thr")
            }

            lifecycleScope.launch {
                mutableSharedFlow2.collect {
                    Log.d(TAG, "collect it2=${it}")
                }
            }

        }

}