package cn.gd.snm.testjetpact.databinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cn.gd.snm.testjetpact.R

/**
 * 测试常用的databinding的使用方式：
 *
 */
class DataBindingActivity : AppCompatActivity() {
    lateinit var mdataBinding:ActivityDataBindingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_data_binding)
        mdataBinding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding)

        //TODO:测试databingding第一种使用方式。
//        test1()

        //TODO: 测试databingding第二种使用方式。
        test2()
    }

    private fun test2() {
        var user2 = User2()
        mdataBinding.user2 = user2
        mdataBinding.bt.setOnClickListener{
            user2.name = "darrentest"
        }
    }

    private fun test1() {
        var user = User()
        mdataBinding.user = user
        mdataBinding.bt.setOnClickListener{
            user.age.set("14")
            user.name.set("wtt")
        }
    }
}