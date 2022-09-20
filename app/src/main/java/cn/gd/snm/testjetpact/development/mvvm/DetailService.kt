package cn.gd.snm.testjetpact.development.mvvm

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * 视频描述测试地址（get）：
 *  http://epg.launcher.aisee.tv/epgol/getsimpledetail?source=snmott&productline=lanxu&channel=10401&ifver=v7&code=jlj6rvcl04ca0ao
 *
 * 视频列表测试地址（get）:
 *  http://epg.launcher.aisee.tv/epgol/getbatchids?source=snmott&productline=lanxu&channel=10401&ifver=v7&code=jlj6rvcl04ca0ao&req_contenttype=2&req_videotype=3&pageno=1&pagesize=180&cp_source=ottjg
 *
 */
interface DetailService {

    @GET("epgol/{code}")
    fun doGetDesData(@Path("code") code:String, @QueryMap map:Map<String,String>): Call<DecsEntity>

    @GET("epgol/{code}")
    fun doGetListData(@Path("code") code:String, @QueryMap map:Map<String,String>): Call<ListInfoEntity>

}