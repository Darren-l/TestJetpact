package cn.gd.snm.testjetpact.development.mvvm

import androidx.lifecycle.ViewModel

class DetailViewModel:ViewModel() {

    val  descLiveData by lazy {
        DecsRepository().fetchData()
    }

    val listLiveData by lazy {
        ListDataRepository().fetchData()
    }

    fun postDescView(entity: DecsEntity){
        descLiveData.postValue(entity)
    }

    fun postRecyclerView(listInfoEntity: ListInfoEntity){
        listLiveData.postValue(listInfoEntity)
    }

}