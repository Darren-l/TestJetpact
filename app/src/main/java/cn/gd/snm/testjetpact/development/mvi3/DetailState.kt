package cn.gd.snm.testjetpact.development.mvi3

import cn.gd.snm.testjetpact.development.mvvm.DecsEntity

/**
 * mvi3：详情页的状态，流的下游数据，用于驱动Ui的刷新。
 *
 */
data class DetailState(
    var hash:Int = 0,
    val isError:Boolean = false,
    val isLoading:Boolean = false,
    val errorMessage:String = "",
    var descData:DecsEntity = DecsEntity()) {
}