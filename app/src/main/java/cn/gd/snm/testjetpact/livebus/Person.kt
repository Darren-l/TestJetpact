package cn.gd.snm.testjetpact.livebus

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class Person: BaseObservable() {

    @set:Bindable
    var pname:String = "darren"
    set(value) {
        field = value
        notifyChange()
    }

    var age:Int = 100
}