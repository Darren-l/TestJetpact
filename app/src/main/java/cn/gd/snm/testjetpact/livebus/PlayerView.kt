package cn.gd.snm.testjetpact.livebus

import androidx.lifecycle.Lifecycle

class PlayerView(var lifecycle: Lifecycle) {

    init {
        initLifeCycle()
    }

    private fun initLifeCycle() {
        lifecycle.addObserver(PlayerLifeCycle())
    }
}