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
class ListDataRepository {

    val mutableLiveData = MutableLiveData<ListInfoEntity>()

    /**
     * 这里实际是异步的，直接返回了mutableLiveData，待请求成功后会调用postValue更新。
     *
     */
    fun fetchData():MutableLiveData<ListInfoEntity>{
        var reqbody2 = mapOf("source" to "snmott",
            "productline" to "lanxu",
            "channel" to "10401",
            "ifver" to "v7",
            "code" to "jlj6rvcl04ca0ao")

        detailService.doGetListData("getbatchids",reqbody2).enqueue(object: Callback<ListInfoEntity> {
            override fun onResponse(call: Call<ListInfoEntity>, response: Response<ListInfoEntity>) {
                Log.d(MvvmActivity.TAG,"onResponse...")
                if(response.isSuccessful){
                    Log.d(MvvmActivity.TAG,response.body().toString())
                    mutableLiveData.postValue(response.body())
                }else{
                    Log.e(MvvmActivity.TAG,"doGet error...")
                }
            }
            override fun onFailure(call: Call<ListInfoEntity>, t: Throwable) {
                Log.e(MvvmActivity.TAG,"onFailure...")
            }
        })

        return mutableLiveData
    }

    private val detailService by lazy {
        var retrofitUtils =   RetrofitUtils.createRetrofit(MvvmActivity.BASE_URL)
        retrofitUtils.create(DetailService::class.java)
    }
}