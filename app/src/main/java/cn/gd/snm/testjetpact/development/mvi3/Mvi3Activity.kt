package cn.gd.snm.testjetpact.development.mvi3

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import kotlinx.android.synthetic.main.activity_mvi.*
import kotlinx.android.synthetic.main.common_layout.*
import kotlinx.android.synthetic.main.common_layout.tv_title
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * 测试MVI框架3：相对于mvi2，将数据驱动意图intent化，intent + flow + state形成一个完整的mvi模式。
 *
 *
 */
class Mvi3Activity : AppCompatActivity() {

    companion object {
        val TAG = Mvi3Activity::class.simpleName
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvi)
        bindData()
    }

    private fun bindData() {
        //todo intent流。
        intents
            .onStart {
                Log.d(TAG, "doGetAidlInfo before..")
                mviViewModel.doGetAidlInfo("test")
                Log.d(TAG, "doGetAidlInfo end..")
            }
            .onEach(mviViewModel::send)
            .launchIn(lifecycleScope)

        //todo 转换后的加载意图数据流。
        launchRepeatOnLifecycle {
            mviViewModel.newState
                .collect {
                    Log.d(TAG, "newState collect.. it=$it")
                    bindDescView(it.descData)
                }
        }

        //todo 定义的加载状态意图数据流。
        launchRepeatOnLifecycle {
            mviViewModel.eventFlow
                .collect {
                    Log.d(TAG, "eventFlow collect.. it=$it")
                    showEvent(it)
                }
        }
    }

    /**
     * 页面加载内容事件。
     *
     */
    private fun bindDescView(it: DecsEntity?) {
        tv_title.text = it!!.detail.title
        tv_score.text = it.detail.score
        tv_year.text = it.detail.year
        tv_desc.text = it.detail.desc
    }


    /**
     * 页面加载状态事件。
     *
     */
    private fun showEvent(it: EventIntent) {
        if (it.isLoadEnd) loading.visibility = View.GONE
        if (it.errorMessage != "") Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
    }


    /**
     * Intent封装的是触发意图行为的参数。如初始化意图，交互意图。
     *
     */
    private val intents by lazy {
        merge(
            flowOf(DetailNewIntent.Init("getsimpledetail")),   //todo 初始化。
            onclickEventFlow   // todo 交互意图。
        )
    }

    /**
     * 点击意图flow。
     *
     */
    private val onclickEventFlow = callbackFlow {
        bt_change1.setOnClickListener {
            Log.d(TAG, "onclickEventFlow...")
            trySend(DetailNewIntent.Event("修改数据1"))
        }
        awaitClose {
            bt_change1.setOnClickListener(null)
        }
    }

    private val mviViewModel by lazy {
        ViewModelProvider(this)[Mvi2ViewModel::class.java]
    }

}