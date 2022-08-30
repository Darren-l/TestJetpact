package cn.gd.snm.testjetpact.viewmodel

import androidx.databinding.ObservableField


class ModelUser{
    var name = ObservableField<String>()
    var age = ObservableField<Int>()

    init {
        this.name.set("默认值")
    }
}