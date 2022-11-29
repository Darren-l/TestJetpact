package cn.gd.snm.testjetpact.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



class RetrofitUtils {

    companion object {

        /**
         * 创建retrofit工厂
         *
         */
        fun createRetrofit(baseUrl: String): Retrofit {
            //todo 自定义okhttp
            var okhttpClient = OkHttpClient.Builder()
            okhttpClient.readTimeout(10L, TimeUnit.SECONDS)
            okhttpClient.connectTimeout(9, TimeUnit.SECONDS)
            okhttpClient.addNetworkInterceptor(CustomLogInterceptor())


            var retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okhttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()

            return retrofit
        }

        fun createRetrofitTestFlow(baseUrl: String): Retrofit {
            //todo 自定义okhttp
            var okhttpClient = OkHttpClient.Builder()
            okhttpClient.readTimeout(10L, TimeUnit.SECONDS)
            okhttpClient.connectTimeout(9, TimeUnit.SECONDS)
            okhttpClient.addNetworkInterceptor(CustomLogInterceptor())


            var retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okhttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit
        }
    }



//    fun createRetrofitByLiveData(baseUrl: String): Retrofit {
//        //todo 自定义okhttp
//        var okhttpClient = OkHttpClient.Builder()
//        okhttpClient.readTimeout(10L, TimeUnit.SECONDS)
//        okhttpClient.connectTimeout(9, TimeUnit.SECONDS)
//        okhttpClient.addNetworkInterceptor(CustomLogInterceptor())
//
//
//        var retrofit = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .client(okhttpClient.build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
//            .build()
//
//        return retrofit
//    }

}