package cn.gd.snm.testjetpact.development.mvi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.development.mvvm.*
import kotlinx.android.synthetic.main.activity_data_binding.*
import kotlinx.android.synthetic.main.common2_layout.*
import kotlinx.android.synthetic.main.common2_layout.tv_title
import kotlinx.android.synthetic.main.common_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 测试MVI架构1  使用stateFlow代替liveData。
 *
 * 与传统的liveData相比，flow将数据与ui的观察者关系转化为流，使整个数据具备流的特性。
 *
 * 传统的livedata：
 *  数据与Ui的关系为被观察者与订阅者。
 *      缺点：
 *          1. 对于多个数据源，不太好进行合流 --- flow方便合流，且可以将异步的数据获取同步化，避免异步问题。。
 *          2. 对于数据源的处理，不太方便  --- flow使用map操作符可以轻松驾驭数据源的处理。
 *          3. 无法监听数据处理的生命周期。  --- flow可以监听数据生命周期。
 *          3. 对于Ui的初始状态，出错状态，异常处理还是散布在各个角落，
 *
 *
 *
 */
class MviActivity : AppCompatActivity() {

    companion object {
        val TAG = MviActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvi)
        initView()
        bindData()
        setListener()
    }

    private fun setListener() {
        bt_change1.setOnClickListener {
            lifeLauncher {
                //todo 注意data数据类，实际上单例。如果需要返回不同对象，则需要进行拷贝，且必须要修改值，否则还
                // 是同一个对象。和livedata不同，返回同一对象，不会通知下游接受者。
                var desc = mviViewModel.descStateFlow.value.copy(cp_source = "这里需要修改下值")
                desc.detail.title = "更新title"
                mviViewModel.descStateFlow.value = desc
            }
        }
    }

    private fun bindData() {
        lifeLauncher {
            Log.d(TAG, "bindData... desc")
            mviViewModel.descStateFlow
                .onStart {
                    //todo 通常设置ui初始化状态

                }
                .onCompletion {
                    //todo 设置ui结束状态

                }.catch {
                    //todo 设置ui出错

                }
                .collect {
                    Log.d(TAG, "bindData... it=$it")
                    bindDescView(it)
                }

        }

        lifeLauncher {
            mviViewModel.recyStateFlow
                .collect {
                    Log.d(TAG, "bindData... recy")
                    bindRecyview(it)
                }
        }
    }

    lateinit var recyAdapter: ItemAdapterByMvi
    private fun bindRecyview(it: ListInfoEntity) {
        Log.d(TAG, "bindRecyview... it=$it")
        if (it.datas.isEmpty()) {
            //todo 数组为空，需要返回，和livedata不同，stateFlow有初始值，若size为空带入adapter导致item的size
            // 值为空，后期recyclerview无法刷新。
            return
        }

        if (rec.adapter == null) {
            recyAdapter = ItemAdapterByMvi(this, it, mviViewModel.recyStateFlow)
            rec.adapter = recyAdapter
        }
        Log.d(TAG, "update...")
        recyAdapter.update(it)
    }

    private fun bindDescView(it: DecsEntity?) {
        tv_title.text = it!!.detail.title
        tv_score.text = it.detail.score
        tv_year.text = it.detail.year
        tv_desc.text = it.detail.desc
    }

    private fun lifeLauncher(call: suspend () -> Unit) {
        lifecycleScope.launch {
            call()
        }
    }

    private fun initView() {
        rec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private val mviViewModel by lazy {
        ViewModelProvider(this)[MviViewModel::class.java]
    }

}