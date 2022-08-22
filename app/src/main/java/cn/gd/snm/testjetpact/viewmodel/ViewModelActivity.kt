package cn.gd.snm.testjetpact.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cn.gd.snm.testjetpact.R
import kotlinx.android.synthetic.main.activity_view_model.*

/**
 * 用于测试viewmodel。
 *
 *  1. viewmodel使用。
 *  2. 顺便复习mvvm的三种使用方式。
 *  3. viewmodel+mvvm的使用。
 *
 *  viewModel也是个键值对，key为lifeCycler，是一个全局的数据存储。
 *
 */
class ViewModelActivity : AppCompatActivity() {

    var contentId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model)
        //TODO:正常设置值，横竖屏切换时，会有contentid值丢失问题。
//        initView()

        //TODO:使用viewmodel，值会被存储在viewModel中，横竖切换时，
        // 值不会丢失
//        testViewModel()

        //TODO:第一种方式实现
        testMvvm()

    }

    private fun testMvvm() {

   }

    lateinit var testViewModel:TestViewModel
    private fun testViewModel() {
        testViewModel = ViewModelProvider(this).get(TestViewModel::class.java)
        tv1.text = testViewModel.contentId.toString()

        bt.setOnClickListener{
            testViewModel.contentId ++
            tv1.text = testViewModel.contentId.toString()
        }

    }

    private fun initView() {
        bt.setOnClickListener{
            contentId ++
            tv1.text = contentId.toString()
        }
    }
}