package cn.gd.snm.testjetpact.development.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.gd.snm.testjetpact.utils.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 具体请求数据执行者。
 *
 */
class DecsRepository {

    val mutableLiveData = MutableLiveData<DecsEntity>()

    /**
     * 这里实际是异步的，直接返回了mutableLiveData，待请求成功后会调用postValue更新。
     *
     */
    fun fetchData():MutableLiveData<DecsEntity>{
        //todo 通常是先访问数据库，加载本地数据。

        //todo 然后进行网路请求，加载网络数据。
        var reqbody = getRequestBody()
        detailService.doGetDesData("getsimpledetail",reqbody).enqueue(object: Callback<DecsEntity> {
            override fun onResponse(call: Call<DecsEntity>, response: Response<DecsEntity>) {
                Log.d(MvvmActivity.TAG,"onResponse...")
                if(response.isSuccessful){
//                    Log.d(MvvmActivity.TAG,response.body().toString())
                    mutableLiveData.postValue(response.body())
                }else{
                    Log.e(MvvmActivity.TAG,"doGet error...")
                }
            }
            override fun onFailure(call: Call<DecsEntity>, t: Throwable) {
                Log.e(MvvmActivity.TAG,"onFailure...")
            }
        })
        return mutableLiveData
    }

    /**
     * 构造请求参数。
     *
     */
    private fun getRequestBody(): Map<String, String> {
        return mapOf("source" to "snmott",
            "productline" to "lanxu",
            "channel" to "10401",
            "ifver" to "v7",
            "code" to "jlj6rvcl04ca0ao",
            "req_contenttype" to "2",
            "pageno" to "1",
            "pagesize" to "180",
            "cp_source" to "ottjg")
    }

    private val detailService by lazy {
        var retrofitUtils =   RetrofitUtils.createRetrofit(MvvmActivity.BASE_URL)
        retrofitUtils.create(DetailService::class.java)
    }
}