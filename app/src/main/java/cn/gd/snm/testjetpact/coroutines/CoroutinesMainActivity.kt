package cn.gd.snm.testjetpact.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.coroutines.flow.Test04
import kotlinx.android.synthetic.main.activity_coroutines_main.*

class CoroutinesMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines_main)

        test()

    }

    private fun test() {

        c_bt.requestFocus()

        c_bt.setOnClickListener{
//            var test01 = Test01()
//            test01.main()

            //todo flow常用操作符
//            var testFlow = Test02(lifecycleScope)
//            testFlow.main()

            //todo
//            var test03 = Test03(lifecycleScope)
//            test03.main()

//            var test04 = Test04Main(lifecycleScope)
//            test04.go()

            var test4 = Test04(lifecycleScope)
            test4.main()

        }


    }
}