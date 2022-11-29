package cn.gd.snm.testjetpact.development.mvi2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * VM层：处理数据流的合流/过滤/变换。
 *
 */
class MviViewModel2 : ViewModel() {

    companion object{
        val TAG = MviViewModel2::class.simpleName
    }

    //todo 这里使用了了stateFlow，自带粘性 + 缓存最后一个。
    val stateFlow = MutableStateFlow(DecsModel())

    /**
     * 将stateFlow转化为新的流，使用懒加载的方式去调用一个对象初始化后的返回值。
     *
     */
    val intentFlow by lazy {
        stateFlow
            .map{
                Log.d(Mvi2Activity.TAG,"map1...mode=${it.loaddingEnd}")
                it.descEntity
            }
            .flowOn(Dispatchers.IO)
            .catch {
                emit(DecsEntity(error = "error log..."))
            }.stateIn(viewModelScope, SharingStarted.Lazily, DecsEntity())
    }

    /**
     * 在流中，发送了另一个流。
     *
     */
    fun send(desc:DecsModel){
        viewModelScope.launch {
            stateFlow.emit(desc)
        }
    }


    /**
     * 注意该方法只能调用一次，因为使用了stateIn对流进行了转化。
     *
     */
    fun callbackFlow(callbackFlow:Flow<DecsModel>) = flowOf(MviDesRepo2().remoteLocalData()
        , MviDesRepo2().remoteNewData()
        ,callbackFlow
        )
        .flattenConcat()
//                .transformWhile{  //todo 一旦过滤，后续点击收不到事件了。
//                    Log.d(TAG,"transformWhile...")
//                    emit(it)
//                    !it.loaddingEnd
//                }
        .map{
            Log.d(Mvi2Activity.TAG,"map1...mode=${it.loaddingEnd}")
            it.descEntity
        }
        .flowOn(Dispatchers.IO)
        .catch {
            emit(DecsEntity(error = "error log..."))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DecsEntity()) //todo 转化为热流。

    /**
     * StateFlow 是一个特别的 SharedFlow，类似LiveData 特性：
     *  1. 总是会缓存1个最新的数据，上游流产生新数据后就会覆盖旧值（LiveData 也是）
     *  2. 持有一个 value 字段，可通过stateFlow.value读取最新值（LiveData 也是）
     *  3. 是粘性的，会将缓存的最新值分发给新订阅者（LiveData 也是）
     *  4. 必须有一个初始值（LiveData 不是）
     *  5. 会过滤重复值，即新值和旧值相同时不更新 -- 同一个对象，不会触发订阅。（LiveData 不是）
     *
     * 注意，尽可能不要把stateIn放到方法中返回，否则每次调用该方法都会返回一个新的flow，除非能确保该方法只调用一次。
     *
     */
    val newFetchDataStateFlow = flowOf(MviDesRepo2().remoteLocalData(), MviDesRepo2().remoteNewData())
        .flattenMerge()
        .transformWhile{
            emit(it)
            !it.loaddingEnd
        }
        .map{
            Log.d(TAG,"map1...mode=${it.loaddingEnd}")
            it.descEntity
        }
        .flowOn(Dispatchers.IO)
        .catch {
            emit(DecsEntity(error = "error log..."))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DecsEntity()) //todo 转化为热流。


    /**
     * 共享热流：可以将冷流转化为热流，并可以根据参数，启动缓存策略。
     *
     * 共享流的特性：可以拥有多个订阅者，可配置缓冲策略。
     *
     */
    val newFetchDataShareFlow = flowOf(MviDesRepo2().remoteLocalData(), MviDesRepo2().remoteNewData())
        .flattenMerge()
        .transformWhile{
            emit(it)
            !it.loaddingEnd
        }
        .map {
            Log.d(TAG,"map1...mode=${it.loaddingEnd}")
            it.descEntity
        }.flowOn(Dispatchers.IO)
        .catch {
            emit(DecsEntity(error = "error log..."))
        }.shareIn(viewModelScope, SharingStarted.Lazily)//todo 转化为热流。


    /**
     * 正常的flow是冷流，只有collect才会触发数据流，且整个流过程中并没有存储数据。这会导致每次触发生命周期，都会
     * 触发一次数据流，触发请求。
     *
     */
    //todo： 数据源通常有两个，一个是本地数据库的，一个是网络的。
    fun newFetchData() = flowOf(MviDesRepo2().remoteLocalData(), MviDesRepo2().remoteNewData())
//        .flattenConcat()  //todo 如果是串行则不需要过滤,请求会一个一个执行。

        //todo 如果是并行，则增加一个过滤，防止异步的问题数据库覆盖了请求的数据。
        .flattenMerge()
        .transformWhile{    //todo 自动转发上游数据，直到loadingEnd为true后停止。
            emit(it)
            !it.loaddingEnd
        }
        .map {
            Log.d(TAG,"map1...mode=${it.loaddingEnd}")
            it.descEntity
        }.flowOn(Dispatchers.IO)
        .catch {
            //todo 捕获异常。
        }

}