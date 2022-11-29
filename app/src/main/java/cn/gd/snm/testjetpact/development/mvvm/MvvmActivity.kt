package cn.gd.snm.testjetpact.development.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.gd.snm.testjetpact.R
import kotlinx.android.synthetic.main.common2_layout.*
import kotlinx.android.synthetic.main.common_layout.*
import kotlinx.android.synthetic.main.common_layout.tv_title

/**
 * MVVM + viewModel + liveData
 *
 * ### mvvm中数据的初始化方式--命令式：
 *
 *  由V层 -> viewModel层 -> repos -> liveModel
 *
 * ### View层Ui的刷新方式--订阅式：
 *
 *  由V层主动观察liveModel的数据变化后刷新。
 *
 *
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
        initView()
        bindData()
        setListener()
    }


    private fun initView() {
        rec.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
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
     * 数据与视图的绑定，页面事件的触发由act -> viewModle -> repos -> model
     *
     */
    private fun bindData() {
        detailViewModel.descLiveData.observe(this){
            Log.d(TAG,"descLiveData,observe...decsEntity=$it")
            bindDescView(it)
        }

        detailViewModel.listLiveData.observe(this){
            Log.d(TAG,"listLiveData,observe...")
            bindRecyclerView(it)
        }
    }

    lateinit var recyAdapter:ItemAdapter
    /**
     * 更新recyclerview组件
     *
     */
    private fun bindRecyclerView(it:ListInfoEntity) {
        if(rec.adapter == null){
            recyAdapter = ItemAdapter(this,it,detailViewModel.listLiveData)
            rec.adapter = recyAdapter
        }
        recyAdapter.update(it)
    }

    /**
     * 更新desc组件的Ui。
     *
     */
    private fun bindDescView(it: DecsEntity?) {
        tv_title.text = it!!.detail.title
        tv_score.text = it.detail.score
        tv_year.text = it.detail.year
        tv_desc.text = it.detail.desc
    }

    private val detailViewModel by lazy{
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

}