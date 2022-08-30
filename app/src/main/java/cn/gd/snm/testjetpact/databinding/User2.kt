package cn.gd.snm.testjetpact.databinding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cn.gd.snm.testjetpact.BR

/**
 *
 * mvvm绑定方式一：
 *
 * 1. 继承BaseObservable
 * 2. notifyChange()刷新该对象下所有的field，notifyPropertyChanged刷新指定id的field。
 *
 * BR为apt自动生成的代码，有点类似于R文件。目的是给刷新提供field对应的id，如果BR文件中没有当前类对应的id，则需要
 * 执行下编译，生成对应id。
 *
 *
 *
 */
class User2:BaseObservable() {

    //xml中有绑定的field
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


    //即使xml中没有绑定field，仍然可以算在BR中生成。
    @get:Bindable
    @set:Bindable
    var name2:String = ""
        get() {
            notifyChange()
            return field
        }
        set(value) {
            field = value
            notifyPropertyChanged(BR.name2)
        }



}