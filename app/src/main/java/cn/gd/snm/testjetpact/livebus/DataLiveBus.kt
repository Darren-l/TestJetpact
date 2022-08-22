package cn.gd.snm.testjetpact.livebus

import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KClass

/**
 * 单例，以keyValue的形式保存所有的DataLive。
 *
 */
class DataLiveBus private constructor(){

    var map:MutableMap<String,MutableLiveData<Any>> = mutableMapOf()

    companion object{

        val instance:DataLiveBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            DataLiveBus()
        }
    }

    //TODO:获取DataLive，如果没有就创建新的。
    fun <M:Any>obtain(key:String,clz: KClass<M>): MutableLiveData<M> {

        if(!map.containsKey(key)){
            map[key] = MutableLiveData()
        }

        return map[key] as MutableLiveData<M>
    }

}