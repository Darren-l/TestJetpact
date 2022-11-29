package cn.gd.snm.testjetpact.development.mvi

import androidx.lifecycle.ViewModel
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import cn.gd.snm.testjetpact.development.mvvm.ListInfoEntity
import kotlinx.coroutines.flow.MutableStateFlow

class MviViewModel: ViewModel() {

    val descStateFlow:MutableStateFlow<DecsEntity> by lazy {
        MviDesRepo().fetchData()
    }

    val recyStateFlow:MutableStateFlow<ListInfoEntity> by lazy {
        MviDesRepo().fetchRecyData()
    }
}