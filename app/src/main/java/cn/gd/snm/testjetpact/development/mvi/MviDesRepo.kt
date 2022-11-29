package cn.gd.snm.testjetpact.development.mvi

import android.util.Log
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import cn.gd.snm.testjetpact.development.mvvm.ListInfoEntity
import cn.gd.snm.testjetpact.development.mvvm.MvvmActivity
import cn.gd.snm.testjetpact.utils.RetrofitUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MviDesRepo {

    companion object {
        val TAG = MviDesRepo::class.simpleName
    }

    var decsMutableStateFlow = MutableStateFlow(DecsEntity())

    /**
     * 对viewModel层暴露获取数据的接口。
     *
     */
    fun fetchData(): MutableStateFlow<DecsEntity> {
        Log.d(TAG, "collect, thread1111=${Thread.currentThread().name}")
        var coroutineScope = CoroutineScope(Job())
        coroutineScope.launch {
            //todo 获取网络数据
            remoteNewData()
//                .map {
//                    //todo 这里测试下retrofit挂起请求，可以将多个异步变成同步
//                    Log.d(TAG, "map1... before")
//                    var desc = detailService.doGetDesData("getsimpledetail", getRequestBody())
//                    delay(1000)
//                    Log.d(TAG, "map1... end desc=$desc")
//                    desc
//                }
//                .map {
//                    Log.d(TAG, "map2... before")
//                    var desc = detailService.doGetDesData("getsimpledetail", getRequestBody())
//                    Log.d(TAG, "map2... end desc=$desc")
//                    desc
//                }
                .flowOn(Dispatchers.IO)
                .collect {
                    Log.d(TAG, "collect, it=$it")
                    Log.d(TAG, "collect, thread=${Thread.currentThread().name}")
                    decsMutableStateFlow.value = it
                }
        }
        return decsMutableStateFlow
    }

    var recyMutableStateFlow = MutableStateFlow(ListInfoEntity())

    fun fetchRecyData():MutableStateFlow<ListInfoEntity>{
        var coroutineScope = CoroutineScope(Job())
        coroutineScope.launch {
            remoteRecyData().flowOn(Dispatchers.IO)
                .collect{
                    Log.d(TAG,"remoteRecyData it=$it")
                    recyMutableStateFlow.value = it
                }
        }
        return recyMutableStateFlow
    }

    private suspend fun remoteRecyData() = suspend {
        var reqbody2 = mapOf("source" to "snmott",
            "productline" to "lanxu",
            "channel" to "10401",
            "ifver" to "v7",
            "code" to "jlj6rvcl04ca0ao")
        detailService.doGetListData("getbatchids",reqbody2)
    }.asFlow()

    private suspend fun remoteNewData() = suspend {
        detailService.doGetDesData("getsimpledetail", getRequestBody())
    }.asFlow()

    private fun getRequestBody(): Map<String, String> {
        return mapOf(
            "source" to "snmott",
            "productline" to "lanxu",
            "channel" to "10401",
            "ifver" to "v7",
            "code" to "jlj6rvcl04ca0ao",
            "req_contenttype" to "2",
            "pageno" to "1",
            "pagesize" to "180",
            "cp_source" to "ottjg"
        )
    }

    private val detailService by lazy {
        var retrofitUtils = RetrofitUtils.createRetrofitTestFlow(MvvmActivity.BASE_URL)
        retrofitUtils.create(MviService::class.java)
    }
}