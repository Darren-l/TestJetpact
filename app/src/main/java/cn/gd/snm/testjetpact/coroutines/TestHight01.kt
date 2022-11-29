package cn.gd.snm.testjetpact.coroutines

import android.util.Log
import androidx.lifecycle.lifecycleScope
import cn.gd.snm.testjetpact.development.mvi3.Mvi3Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * 测试流的类型过滤符
 *
 *
 */
class TestHight01(var lifecycleScope: CoroutineScope) {

    /**
     *
     * filterIsInstance过滤流的类型。
     *
     * 特点：如果符合就进入作用域执行，否则直接拦截，不会在发给下游了。
     *
     */
    fun filterIsInstance() {
        lifecycleScope.launch {
            flowOf(1,"100")
                .onStart {
                    Log.d(Mvi3Activity.TAG, "onStart...")
                }.onEach {
                    Log.d(Mvi3Activity.TAG, "onEach=$it")
                }
                .onCompletion {
                    Log.d(Mvi3Activity.TAG, "onCompletion...")
                }
//                .filterIsInstance<Int>().flatMapConcat {
//                    flow {
//                        emit( it + 10)  //直接发送int型，拦截不会在发给下游。
//                        emit( it.toString() + 20)   //这个下游可以正常收到。
//                    }
//                }
//                .filterIsInstance<String>().flatMapConcat {
//                    flow {
//                        emit( it + 20)
//                    }
//                }
//                .toChangeFlow()
                .collect {
                    Log.d(Mvi3Activity.TAG,"collect it=$it")
                }
        }
    }

    /**
     *
     * 使用merge+filterIsInstance操作符，可以对上游的流进行合并过滤处理。通常用于根据上游流的不同类型，走不同处
     * 理方式，返回同一的流的逻辑。
     *
     * 使用merge需要注意生命周期的处理，原本的流会立刻走completion回调，新的流会重新走onStart。
     *
     */
    private fun Flow<*>.toChangeFlow(): Flow<Int> =
        merge(
            filterIsInstance<Int>().flatMapConcat {
                flow {
                    emit( it + 10)
                }
            }
            ,
            filterIsInstance<String>().flatMapConcat {
                flow {
                    emit(it.toInt() + 20)
                }
            }
        )

}