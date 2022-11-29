package cn.gd.snm.testjetpact.development.mvi3

import cn.gd.snm.testjetpact.development.mvvm.DecsEntity

/**
 * 统一使用枚举类定义行为。
 *
 */
sealed class DetailIntent{

    data class DescInitIntent(val code:String):DetailIntent(){
        //todo 假设这里是默认
        var pos = 0
        var decsEntity = DecsEntity()
        var error = ""
    }
    data class DescEventIntent(val type:Int):DetailIntent()

    data class RecyclerInitIntent(val type:Int):DetailIntent()
    data class RecyclerActionIntent(val type:Int):DetailIntent()
}
