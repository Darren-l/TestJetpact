package cn.gd.snm.testjetpact.development.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import cn.gd.snm.testjetpact.R
import kotlinx.android.synthetic.main.common_layout.*

/**
 * MVVM + viewModel + liveData
 *
 */
class MvvmActivity : AppCompatActivity() {

    companion object{
        val BASE_URL = "http://epg.launcher.aisee.tv/"
        val TAG = this::class.simpleName + "####"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvvm)
        bindData()
        setListener()
    }

    /**
     * 设置监听事件
     *
     */
    private fun setListener(){
        //todo 通常页面点击时，会有选择状态等需要刷新Ui,这里模拟刷新title
        bt_change1.setOnClickListener {
            val data = detailViewModel.descLiveData.value
            data?.detail?.title = "修改标题"
            detailViewModel.postDescView(data!!)
        }
    }

    /**
     * 数据与视图的绑定
     *
     */
    private fun bindData() {
        detailViewModel.descLiveData.observe(this){
            Log.d(TAG,"descLiveData,observe...decsEntity=$it")
            updateDescView(it)
        }

        detailViewModel.listLiveData.observe(this){
            Log.d(TAG,"listLiveData,observe...listInfoEntity=$it")
            updateRecyclerView()
        }
    }

    /**
     * 更新recyclerview组件
     *
     */
    private fun updateRecyclerView() {

    }

    /**
     * 更新desc组件的Ui。
     *
     */
    private fun updateDescView(it: DecsEntity?) {
        tv_title.text = it!!.detail.title
        tv_score.text = it.detail.score
        tv_year.text = it.detail.year
        tv_desc.text = it.detail.desc
    }

    private val detailViewModel by lazy{
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

}