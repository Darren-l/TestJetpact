package cn.gd.snm.testjetpact.development.mvi3


/**
 * mvi3：流的上游intent类型，触发UI刷新的行为有初始化和点击交互事件。
 *
 */
sealed class DetailNewIntent{
    data class Init(val code:String):DetailNewIntent()
    data class Event(val title:String):DetailNewIntent()
}