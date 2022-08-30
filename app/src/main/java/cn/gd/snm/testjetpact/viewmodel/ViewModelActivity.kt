package cn.gd.snm.testjetpact.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.databinding.ActivityViewModelLayBinding
import kotlinx.android.synthetic.main.activity_view_model_lay.*

/**
 * 测试viewModel
 *
 *  viewModel也是个类似于键值对，key为包类名，是一个全局的数据存储，一个activity理论上只持有一个viewModel，
 *  用于持有数据如liveData。
 *
 *  viewModel会在ondes生命周期中自动释放，大小屏切换时页面也会走ondes，可以正常获取到值的原因是，ViewModelProvider
 *  在get时，会先去cacheData页面临时存储区寻找是否存在viewModel，切换前的model会存在cacheData中。
 *
 *
 */
class ViewModelActivity : AppCompatActivity() {

    companion object{
        val TAG = ViewModelActivity::class.simpleName + "####"

    }

    var contentId = 0

    //todo 创建xml后，apt会自动扫描layout目录下所有layout布局和布局中定义的data，并且自动生成xml对应的Binding类，
    // 如 activity_view_model_lay 对应生成ActivityViewModelLayBinding。类型一定不能定义错，否则找不到data。
    lateinit var viewDataBinding:ActivityViewModelLayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_view_model_lay)
        //TODO:正常设置值，横竖屏切换时，会有contentid值丢失问题。
//        initView()

        //TODO:使用viewmodel，值会被存储在viewModel中，横竖切换时，值不会丢失
//        testViewModel()

        //TODO: viewModel + liveData + mvvm
        testMvvm()

    }

    lateinit var testDataViewModel:TestDataViewModel
    var count = 0
    private fun testMvvm() {
        viewDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_view_model_lay)

        //todo #### 1. 测试双向绑定
        //todo 应加载和dataBindding对应的布局
        var modelUser = ModelUser()
        viewDataBinding.modelUser = modelUser

        bt.setOnClickListener{
            modelUser.name.set("修改name的数据值")
        }

        //todo #### 2. 测试融入ViewModel
        testDataViewModel = ViewModelProvider(this).get(TestDataViewModel::class.java)
        Log.d(TAG,"testid = ${testDataViewModel.testId}")
        bt.setOnClickListener{
            testDataViewModel.testId ++
        }

        //todo #### 3. 测试融入liveData到viewModel
        var modelUser2 = ModelUser()
        modelUser2.name.set("modelUser2")
        testDataViewModel.modelUserLd.value = modelUser2

        //todo 注意，这里监听的是modelUserLd是否有被替换，所以如果修改对象中的值，实际上是无效的。
        testDataViewModel.modelUserLd.observe(this){
            //监听modelUserLd修改
            Log.d(TAG,"监听到值的修改，更新textView...")
            viewDataBinding.modelUser = modelUser2  //首次数据准备好后，就绑定。
        }

        //值必须要有修改才会通知观察者。
        bt.setOnClickListener{
            //todo 想让观察者收到消息，必须要更新掉一整个对象，而非对象中的值。
//            var modelUser2 = ModelUser()
//            modelUser2.name.set("modelUser4444")
            //todo 替换livedata中的存储对象，观察者即可收到消息
//            testDataViewModel.modelUserLd.value = modelUser2

            //mvvm 绑定后修改data值，控件即刻变化。
            count ++
            testDataViewModel.modelUserLd.value!!.name.set("点击修改值... $count")

            Log.d(TAG,"onclick end....")
        }
   }

    lateinit var testViewModel:TestViewModel
    private fun testViewModel() {
        setContentView(R.layout.activity_view_model_lay)
        //todo  使用viewModelProvider的方式获取viewModel，而非new。
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