package cn.gd.snm.testjetpact.livebus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import cn.gd.snm.testjetpact.R

/**
 * 用于测试上一个页面liveData观察者回调的调用。
 *
 */
class MainActivity2 : AppCompatActivity() {

    lateinit var liveData: MutableLiveData<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        liveData = DataLiveBus.instance.obtain("main1",String::class)

        testChangeFirstActValue()

    }

    /**
     *  测试在第二个页面设置第一个页面的liveData。
     *
     *  回调时机：
     *      情况一：如果第一个页面还处于活动状态（两个页面交替的短时间内第一个还处于活动状态），那么对应的liveData会被直接调用观察回调。
     *
     *      情况二：如果第一个页面不处于活动状态，那么观察回调不会立刻调用，因为当前次的回调会被页面非活动状态给返回。等到返回时经历
     *  第一个页面的onresume生命周期时才会被调用。
     *
     *      如果是情况一，理论上本来应该会被回调两次，一次是处于第二个页面时的调用，然后是返回时生命周期的调用。但实际上只会调用一次，
     *  原因是源码中对于通知事件有标志位做处理，保证不会重复调用。
     *
     *
     *
     */
    private fun testChangeFirstActValue() {
        Handler(Looper.getMainLooper()).postDelayed({

            liveData.postValue("SecAct..")

        },3000)
    }
}