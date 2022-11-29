package cn.gd.snm.testjetpact.development.mvi2

import android.util.Log
import cn.gd.snm.testjetpact.development.mvi.MviService
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import cn.gd.snm.testjetpact.development.mvvm.ListInfoEntity
import cn.gd.snm.testjetpact.development.mvvm.MvvmActivity
import cn.gd.snm.testjetpact.utils.RetrofitUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * M-Repo层：将原始的数据转化为流，可以对数据类进行包装类，标记该流的类别。
 *
 */
class MviDesRepo2 {

    companion object {
        val TAG = MviDesRepo2::class.simpleName
    }

    fun remoteNewDataByParam(code:String) = suspend {
        Log.d(TAG,"doGetDesData...")
        var desc = detailService.doGetDesData(code, getRequestBody())
        Log.d(TAG,"doGetDesData...end...desc=$desc")
        desc
    }.asFlow().map {
        DecsModel(true,it)  //todo true是对流进行标记，表示该流为请求，后面合流的时候需要使用。
    }

    fun remoteNewData() = suspend {
        Log.d(TAG,"doGetDesData...")
        var desc = detailService.doGetDesData("getsimpledetail", getRequestBody())
        Log.d(TAG,"doGetDesData...end...desc=$desc")
        desc
    }.asFlow().map {
        DecsModel(true,it)  //todo true是对流进行标记，表示该流为请求，后面合流的时候需要使用。
    }

    fun remoteLocalData() = suspend {
        //todo 假设这里做的是数据库的读取。
        Log.d(TAG,"remoteLocalData...")
//        delay(1000)
        var desc = detailService.doGetDesData("getsimpledetail", getRequestBody())
        Log.d(TAG,"remoteLocalData...end=$desc")
        desc
    }.asFlow().map {
        DecsModel(false,it)
    }

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