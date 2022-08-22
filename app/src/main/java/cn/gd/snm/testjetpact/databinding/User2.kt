package cn.gd.snm.testjetpact.databinding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cn.gd.snm.testjetpact.BR

class User2:BaseObservable() {

    @get:Bindable
    @set:Bindable
    var name:String = ""
    get() {
        notifyChange()
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.name)
    }

}