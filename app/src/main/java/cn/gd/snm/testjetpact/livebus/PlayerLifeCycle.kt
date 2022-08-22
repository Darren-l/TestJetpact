package cn.gd.snm.testjetpact.livebus

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@SuppressLint("RestrictedApi")
class PlayerLifeCycle:GenericLifecycleObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d("darren","PlayerLifeCycle event=${event}")
        if(event == Lifecycle.Event.ON_PAUSE){

        }
    }
}