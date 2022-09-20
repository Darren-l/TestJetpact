package cn.gd.snm.testjetpact.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * viewMode在mvvm架构中，作用是作为数据的持有者。
 *
 * LiveData绑定的数据应为唯一数据源。同一类Ui刷新，绑定一个数据源对象，若存在多个状态，则在数据源对象中声明多个状态成员。
 *
 * 更新唯一数据源的方式，
 *
 */
class TestDataViewModel:ViewModel() {
    var testId:Int = 10

    var nameLd:MutableLiveData<String> = MutableLiveData()
    var modelUserLd:MutableLiveData<ModelUser> = MutableLiveData()


    /**
     * 更新唯一数据源时，不管是对象还是基本数据类型，都应该使用Post更新整个对象，所谓唯一数据源指的是Livedata对象，
     * 而非liveData中包裹的对象。
     *
     */
    fun postTestId(){
        nameLd.postValue("123")

    }

    fun postUser(){
        //todo 通常可以直接先从liveData中把值get出来，然后赋值对应状态后再post丢回去，这里我们直接new一个。
        modelUserLd.postValue(ModelUser())

    }


}