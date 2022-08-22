package cn.gd.snm.testjetpact.livebus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.gd.snm.testjetpact.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 目的：
 *  1. 测试liveData的mvvm模式。
 *  2. 查看liveData源码实现及lifecycle的实现。
 *  3. 测试liveData的跨组件传递。（目的应该是为了实现在下一个页面修改上一个页面的值）
 *  3. 解除liveData粘性问题。
 *
 *
 * liveData作用：
 *  1. 观察者模式下，以通知的方式改变视图。
 *
 * liveData可以理解为一个带有观察者功能的bean。
 *
 */

class MainActivity : AppCompatActivity() {

    lateinit var liveData:MutableLiveData<String>

    companion object{

        val TAG = MainActivity::class.simpleName + "##Darren"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        liveData = DataLiveBus.instance.obtain("main1",String::class)

        initView()

        //TODO:测试声明LifeData。
        testLiftData1()

        //TODO:测试监听Act生命周期。
        testLifeCycle()
    }


    private fun testLiftData1() {
        //TODO: 先注册观察者，后通过按键修改值设置值。
        liveData.observe(this){
                value ->
            Log.d(TAG,"obs1#####t=${value}")
            tv1.text = value
        }

        //TODO: 可以同时注册多个，上一个被注册的不会被覆盖。
        liveData.observe(this){
                value ->
            Log.d(TAG,"obs2#####t=${value}")
        }

    }


    private fun initView() {

        testObs.setOnClickListener{
            //TODO:非主线程中，可以使用postValue。
            liveData.postValue("1111")
//            //TODO:主线程可以使用setValue。
////            liveData.value = "222"
//
//            //TODO:测试先setValue，后注册观察者。
//            testLiftData2()
//
//            //TODO: 测试跳转后修改值，查看当前页面UI是否会刷新。
            startActivity(Intent(this,MainActivity2::class.java))

//            testLifeCycle()
        }
    }

    private fun testLiftData2() {
        //TODO:先postValue，后注册观察者，理论上普通的观察者是监听不到事件的，
        // 因为事件的发生在注册之前。而liveData实际是可以收到的，这就是粘性的特点。
        liveData.observe(this){
                value ->
            Log.d("darren","obs1#####t=${value}")
            tv1.text = value
        }

        //TODO:测试多个观察者。
//        liveData.observe(this){
//                value ->
//            Log.d("darren","obs2#####t=${value}")
//        }
    }


    private fun testLifeCycle() {
        lifecycle.addObserver(PlayerLifeCycle())
    }

}