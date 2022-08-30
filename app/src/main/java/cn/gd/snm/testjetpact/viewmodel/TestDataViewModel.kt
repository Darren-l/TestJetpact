package cn.gd.snm.testjetpact.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * viewMode在mvvm架构中，作用是作为数据的持有者。
 *
 */
class TestDataViewModel:ViewModel() {
    var testId:Int = 10

    var nameLd:MutableLiveData<String> = MutableLiveData()
    var modelUserLd:MutableLiveData<ModelUser> = MutableLiveData()

}