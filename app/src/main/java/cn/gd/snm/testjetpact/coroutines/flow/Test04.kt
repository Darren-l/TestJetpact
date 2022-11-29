package cn.gd.snm.testjetpact.coroutines.flow

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class Test04(var lifeCoroutineScope: CoroutineScope) {

    companion object {
        var TAG = this.javaClass.simpleName + "##"
    }


    fun main(){
        Log.d(TAG,"main...")

        //todo 探究suspend函数作用域声明及调用方式。
        testSuspendFun()


    }

    private fun testSuspendFun() {
        // todo 不会执行挂起函数作用域中的代码。
//        testSusp(1)

        //todo invoke调用后，会执行作用域中的代码。
        lifeCoroutineScope.launch {
            testSusp(1).invoke()
        }

    }

    /**
     * 使用suspend声明的为一个挂起函数作用域，需要调用invoke才会执行作用域中的代码。
     *
     * suspend () -> R
     *
     * suspend()为挂起函数作用域。
     *
     */
    fun testSusp(id:Int) = suspend {
        Log.d(TAG,"testSusp....")
        test1()
    }

    suspend fun test1(){
        delay(100)
        Log.d(TAG,"test1...")
    }
}