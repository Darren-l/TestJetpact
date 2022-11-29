package cn.gd.snm.testjetpact.development.mvi3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.gd.snm.testjetpact.development.mvi2.Mvi2Activity
import cn.gd.snm.testjetpact.development.mvi2.MviDesRepo2
import kotlinx.android.synthetic.main.common_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.thread


/**
 * ViewModel主要是处理流的定义和变换。
 *
 *
 */
class Mvi2ViewModel : ViewModel() {
    companion object {
        val TAG = Mvi2ViewModel::class.simpleName
    }

    private val detailIntent = MutableSharedFlow<DetailNewIntent>()

    private val eventChannel = Channel<EventIntent>()
    val eventFlow = eventChannel.receiveAsFlow()


    /**
     * 定义流处理整个流程。
     *
     * stateIn转化为热流有两个原因：
     *  1. stateFlow可以缓存最后一个流数据。
     *  2. 可以被多次订阅，可以设置数据缓存配置。
     *
     */
    val newState = detailIntent
        .onStart {
            Log.d(TAG, "### onStart... ")
        }
        .onEach {
            Log.d(TAG, "### detailIntent.onEach..")
        }
        .onCompletion {
            Log.d(TAG, "### onCompletion...")
        }
        .toChangeFlow()
        .toLoadStatusFlow()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, DetailState())


    /**
     * 定义流的发送事件，流的实际变换由detailIntent定义
     *
     */
    fun send(intent: DetailNewIntent) {
        viewModelScope.launch {
            Log.d(TAG, "send...")
            detailIntent.emit(intent)
        }
    }


    /**
     * 定义一个页面加载状态流，加载失败/成功/出错。
     *
     */
    private fun Flow<DetailState>.toLoadStatusFlow(): Flow<DetailState> = onEach {
        eventChannel.send(EventIntent(it.errorMessage, true))
    }


    /**
     * 使用过滤操作符，对不同数据类型的流数据，进行转化操作。
     *
     * merge：合并流操作符，可以接受多个流，最后整合为一个大流。
     * filterIsInstance：过滤操作符，可以根据流的类型进行过滤。
     * flatMapConcat：处理上游数据，并插入新的流事件。
     *
     * 如果单纯的使用filterIsInstance操作符，无法做到根据不同的类型处理不同的流，因为过滤操作符的调用是线性的，
     * 一旦遇到不符合条件流就会被拦截，不会在发给下游了，而我们的需求是对指定的type类型做处理，不符合要求的直接忽略。
     *
     * 使用merge+filterIsInstance，可以根据上游不同的type类型，对流做不同的处理。
     *
     * 需要注意的是merge操作符会引起上游流的生命周期重新调用问题，调用的次数==合并流的个数。
     *
     */
    private fun Flow<DetailNewIntent>.toChangeFlow(): Flow<DetailState> =
        merge(
            filterIsInstance<DetailNewIntent.Init>().flatMapConcat {
                Log.d(TAG, "toChangeFlow Init... it=$it")
                it.toChangeInitFlow()
            },
            filterIsInstance<DetailNewIntent.Event>().flatMapConcat {
                Log.d(TAG, "toChangeFlow Event...it=$it")
                it.toChangeEventFlow()
            }
        )

    var count = 0

    /**
     * intent类型是event，则走event处理逻辑，这里还缺少了一个当前值的获取。
     *
     */
    private fun DetailNewIntent.Event.toChangeEventFlow(): Flow<DetailState> =
        flow {
            Log.d(TAG, "toChangeEventFlow...")
            //todo stateFlow的特点是，当前对象中的成员必须要有改变，否则会屏蔽上游的数据流。
            var state = newState.value.copy(hash = count)
            state.descData.detail.title = this@toChangeEventFlow.title + count++
            emit(state)
        }


    /**
     * intent的类型是初始化，则走初始化的流处理逻辑。
     *
     */
    private fun DetailNewIntent.Init.toChangeInitFlow(): Flow<DetailState> = flowOf(
        MviDesRepo2().remoteLocalData(),
        MviDesRepo2().remoteNewDataByParam(this.code)
    )
        .flattenMerge()
        .transformWhile {  //todo 这里可以过滤，因为当前事件的流被单独出来了，不会影响到主流。
            emit(it)
            !it.loaddingEnd
        }
        .map {
            Log.d(Mvi2Activity.TAG, "map1...mode=${it.loaddingEnd}")
            var detailState = DetailState()
            detailState.descData = it.descEntity
            detailState
        }
        .flowOn(Dispatchers.IO)
        .catch {
            var errorDetailState = DetailState(isError = true, errorMessage = "出错了")
            emit(errorDetailState)
        }

    /**
     * 这个异步模拟aidl信息的获取，仅仅只需要将函数转化为挂起函数即可，所有的异步函数都可以通过该方式转化为挂起函数，
     * 目的是让该函数在流中进行“同步”。
     *
     */
    private fun getAidlInfo(callback: (info: BpBinderInfo) -> Unit) {
        thread {
            //模拟异步数据的获取
            Thread.sleep(2000)
            var bpBinderInfo = BpBinderInfo("darren", 18)
            callback(bpBinderInfo)
        }
    }

    /**
     * 通常在流发起之前，都会有些异步的准备工作，如aidl bpBinder的获取，可以将函数转化为挂起函数从而实现异步在流
     * 中的同步。
     *
     */
    suspend fun doGetAidlInfo(value: String) =
        suspendCancellableCoroutine<BpBinderInfo> { continuation ->
            getAidlInfo {
                Log.d(TAG,"getAidlInfo successful,it=$it")
                continuation.resume(it){ it ->
                    Log.d(TAG, "throwable=${it.printStackTrace()}")
                }
            }
        }
}