package cn.gd.snm.testjetpact.development.mvi2

import cn.gd.snm.testjetpact.development.mvvm.DecsEntity

/**
 * 对数据entity进行包装，加入加载状态。
 *
 */
data class DecsModel(
    var loaddingEnd:Boolean = false,
    var descEntity: DecsEntity = DecsEntity()
)
