package cn.gd.snm.testjetpact.development.mvi2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.development.mvi.MviViewModel
import cn.gd.snm.testjetpact.development.mvvm.DecsEntity
import cn.gd.snm.testjetpact.development.mvvm.Detail
import kotlinx.android.synthetic.main.activity_mvi.*
import kotlinx.android.synthetic.main.common2_layout.*
import kotlinx.android.synthetic.main.common2_layout.tv_title
import kotlinx.android.synthetic.main.common_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 测试MVI框架2，将驱动显示Ui的方式全部flow化。
 *
 * mvi架构分层：
 *  Repository：负责数据获取，将获取的数据转化为原始的数据流。
 *  ViewModel：根据需求，对数据流进行合流及变换。
 *  view：负责订阅及Ui的刷新。
 *
 * mvvm的实质是，将Ui设置为数据的观察者，数据变化时主动刷新自己。使用liveData+viewModel+flow+retrofit可以极大程度
 * 上简化了业务的复杂性。flow+retrofit让开发者能轻松驾驭服务器数据和本地数据的获取，liveData让开发者仅仅只需要关注数据
 * ，不需要关注ui刷新，简化了数据与Ui交互复杂程度。
 *
 * 当前测试demo仅仅只是一个Mvi的雏形。实质是将数据驱动Ui显示的方式封装成不同的flow数据流，然后对不同的数据流进行统
 * 一处理转换。此时代码架构通常就变成了viewModel+flow+retrofit。其中viewModel负责flow的申明和转化，flow负责在vm
 * 中定义显示意图的类型，如初始化、分页的二次加载、交互的点击事件等。
 *
 * 而一个完整的mvi，不仅仅只是对数据驱动ui刷新的方式进行封装即可，flow流也需要遵循从intent到status的转化。这个放到
 * 下一个activity中说明。
 *
 *
 */
class Mvi2Activity : AppCompatActivity() {

    companion object {
        val TAG = Mvi2Activity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvi)
        bindData()
    }

    /**
     * lifecycleScope.launch：
     *  立刻启动协程，并在生命周期DESTROYED时取消协程。假设act被压在栈后，此时有流从上游发送过来，出发了刷新页面，
     * 此时会crash。
     *
     * repeatOnLifecycle：
     *  执行生命周期的执行，达到Lifecycle.State.STARTED状态才执行，否则取消执行。
     *
     * 相对于普通的flow，shareFlow特点：
     *  1. 可多次订阅。
     *  2. 热流，但可以配置缓冲，带粘性发送给订阅者。
     *
     *
     * 相对于shareFlw，stateFlow特点：
     *  1. 类似与liveData存有一个值。
     *
     * callbackFlow：可以用于异步行为，封装异步数据流，合并在flowOf的数据源中。流在创建时期就需要定义好数据源，所以异
     * 步的数据可以使用该方式先定义。不同的行为可以定义多个callbackFlow，统一在flowOf中合并。
     *
     */
    private fun bindData() {
        //todo 测试普通的flow流
//        initCommonFlow()

        //todo 测试shareFlow -- 相比普通的flow，它可以多次订阅。热流，带缓存和粘性。
//        initShareFlow()

        //todo 测试stateFlow -- 存储最后一只值，以供获取。
//        initStateFlow()

        //todo 测试callbackFlow -- 添加一个异步发送的数据流,合并在数据源中。
//        testCallBackFlow()

        //todo 继续优化mvi结构，流的定义与intent分开。
        testLast()

        //todo 测试flow
//        testFlowReturn()

//        testMutableFlow()

    }

    /**
     * 顺带再次测试下MutableStateFlow的特性。
     *
     */
    private fun testMutableFlow() {

        //todo 拥有liveData特性。
//        thread {
//            Thread.sleep(4000)
//            Log.d(TAG, "thread...")
//            mviViewModel.stateFlow.value = DecsModel(descEntity= DecsEntity(cp_source = "尝试修改111"))
//        }

        //todo 本身是热流，collect本身能收到原因是因为有粘性，且这个粘性对应的缓存区可以自定义。
        lifecycleScope.launch {

            var mutableStateFlow = MutableStateFlow(1)
            delay(1000)
            mutableStateFlow.emit(2)
            delay(1000)
            mutableStateFlow.emit(3)
            delay(1000)
            mutableStateFlow.emit(4)

            //todo 只会收到最后一次数据
            mutableStateFlow.collect {
                Log.d(TAG, "mutableStateFlow it=$it")
            }
        }
    }

    /**
     *  mutableStateFlow和flow2是不同的流，flow2作为mutableStateFlow的下游。
     *
     */
    private fun testFlowReturn() {
        var mutableStateFlow = MutableStateFlow(1)

        lifecycleScope.launch {
            //todo map操作符实际上返回了一个新的Flow
//            var flow2 = mutableStateFlow.map {
//                it.toString()
//            }
            //todo onstart操作符也是一样。
            var flow2 = mutableStateFlow.onStart {
                Log.d(TAG, "flow2 onStart...")
            }

            flow2.collect {
                Log.d(TAG, "flow2 it=$it")
                Log.d(
                    TAG, "mutableStateFlow it=$it, " +
                            "muHash=${mutableStateFlow.hashCode()},flow2Hash=${flow2.hashCode()}"
                )
            }

        }

        lifecycleScope.launch {
            mutableStateFlow.collect {
                Log.d(
                    TAG, "mutableStateFlow it=$it, " +
                            "muHash=${mutableStateFlow.hashCode()},flow2Hash=$"
                )
            }
        }

    }


    /**
     * 思考intents、stateFlow、intentFlow三者关系？
     *
     * intents：最初始的数据源所在的流。
     * stateFlow：新定义的流，接受初始数据源数据发射，目的是根据初始源做不同事务，对流进行转化。
     * intentFlow：将stateFlow作为上游，接受了一个新的流，并暴露给外部进行监听。
     *
     * 思路：
     *  在testLast()测试中，每一个层事务的数据源，都被定义为一个单独的流。需要转化时，则使用新的流进行接受后发送，在新
     *  的流中处理。
     *
     *  这样的好处就是每一个流，相应只处理一层的事务，相当于把这层抽象出来了。如发生错误时，根据当前是哪个流的捕获，就可以
     *  很直观的判断是哪层出的问题，从而可以选择是否跳过这个错误，或者发射给下游做处理。
     *
     *  从流的抽象角度看，testLast()测试就已经是个标准的demo，唯一差的就是对数据intent的封装，一个完整的mvi应该是发出
     *  intent，反馈回一个status。
     *
     */
    private fun testLast() {
        //todo 必须要调用launchIn进行发射，intents本身是冷流，只有订阅后才会触发onEach。
        intents
            .onEach(mviViewModel::send) //todo 轮询值，并定义了一个新的流去处理实际的逻辑。
            .onStart {
                Log.d(TAG, "onStart...")
            }
            .catch {
                Log.d(TAG, "catch...")
            }
            .launchIn(lifecycleScope)   //todo 这里做一个忽略性的订阅，忽略发射的值，但如果异常，可以走到定义个这个流中。


        //todo 尝试接受忽略的发射值。
//        lifecycleScope.launch {
//            mviViewModel.stateFlow.collect {
//                Log.d(TAG,"stateFlow collect=$it")
//            }
//        }

        //todo intents订阅后，走到onEach中，调用mviViewModel对象的send函数，在函数中使用了stateFlow进行发送，
        // intentFlow以stateFlow为上游，对流进行了变换，返回了新的流。这里订阅就是intentFlow的下游，消费上游数据。
        lifecycleScope.launch {
            //todo stateFlow带粘性，会发送最后一个缓存，转化inState后返回给intentFlow。
            mviViewModel.intentFlow
                .collect {
                    Log.d(TAG, "testAll,collect...it=$it")
                    loading.visibility = View.GONE
                    bindDescView(it)
                }
        }


    }

    /**
     * flowOf{} 和merge区别。
     *
     * merge：参数是Flow<T>，返回值是Flow<T>。
     * flowOf：参数是T，返回值是Flow<T>。
     *
     * 这也意味着，如果参数是Flow，那么使用flowOf，返回值会是Flow<Flow<T>>
     *
     * 两者都是返回flow，flowOf是用于发射普通的数据源，而merge是用于发射Flow的数据源。
     *
     */
    private val intents:Flow<DecsModel> by lazy {
        //todo 返回值：Flow<DecsModel>
        merge(
            MviDesRepo2().remoteLocalData(),
            MviDesRepo2().remoteNewData(),
            loadMoreFlow2,
        )

        //todo 返回值：Flow<Flow<DecsModel>>
//        flowOf(MviDesRepo2().remoteLocalData(),
//            MviDesRepo2().remoteNewData(),
//            loadMoreFlow2)


    }

    private val loadMoreFlow2 = callbackFlow {
        bt_change1.setOnClickListener {
            val data = mviViewModel.intentFlow.value.copy(cp_source = "$count")
            data.detail.title = "修改数据${count++}"
            trySend(DecsModel(true, data))
        }
        awaitClose {
            bt_change1.setOnClickListener(null)
        }
    }


    lateinit var testCbFlow: StateFlow<DecsEntity>

    /**
     * 流的特点在于：只能在flow内部自动发送或emit手动发送数据。假设存在一个异步的发送事件，如点击某个item刷新对应的ui
     * 状态，这就需要使用callbackFlow将这个事件提前声明在数据源中。
     *
     * 需要注意的是transformWhile的使用，该操作符一旦满足条件，会过滤后续所有的发射事件，若存在持续的异步事件流，不能
     * 使用该操作符设置过滤条件。
     *
     */
    private fun testCallBackFlow() {
        lifecycleScope.launch {
            testCbFlow = mviViewModel.callbackFlow(loadMoreFlow)

            testCbFlow.onStart {
                loading.visibility = View.VISIBLE
            }
                .onCompletion {
                    //todo 若存在callbackFlow，则流不会结束。
//                    loading.visibility = View.GONE
                }
                .collect {
                    Log.d(TAG, "collect test. it=$it")
                    bindDescView(it)

                    loading.visibility = View.GONE
                }
        }
    }

    private var count = 0

    /**
     * 封装异步数据流，这里封装了点击产生的数据流事件。
     *
     */
    private val loadMoreFlow = callbackFlow {
        bt_change1.setOnClickListener {
            //todo 这里的flow对应是stateFlow，具备stateFlow的特性，如果对应是dataClass，那么必须要更新根元素才会
            // 有发送流的效果，否则stateFlow会认为没有更新数据。
            val data = testCbFlow.value.copy(cp_source = "$count")
            data.detail.title = "修改数据${count++}"
            trySend(DecsModel(true, data))
        }
        awaitClose {
            bt_change1.setOnClickListener(null)
        }
    }

    /**
     * MutableSharedFlow功能类似于liveData，可以通过赋值的方式，重新发送数据流到下游刷新Ui。
     *
     * 而stateIn转化的flow是StateFlow非MutableSharedFlow，StateFlow的value声明为val，不可以通过赋值的方式重新
     * 发送数据流。
     *
     * 实际开发中，我们通常需要在flow的外部发送新的数据流，目的是更新异步的刷新，如点击button显示对应控件效果。这里就需
     * 要用到callbackFlow。
     *
     */
    private fun initStateFlow() {
        lifecycleScope.launch {
            mviViewModel.newFetchDataStateFlow
                .onStart {
                    loading.visibility = View.VISIBLE
                }
                .onCompletion {
                    loading.visibility = View.GONE
                }
                .collect {
                    Log.d(TAG, "newFetchDataStateFlow collect1 it=$it")
                    bindDescView(it)
                }
        }

        bt_change1.setOnClickListener {
            Log.d(TAG, "onclick...")
            lifecycleScope.launch {
                //todo 转化返回的flow是StateFlow非MutableSharedFlow,value对应的声明是val，这会导致无法直接
                // 通过赋值的方式，重新发送数据流。
//                mviViewModel.newFetchDataStateFlow.value = DecsEntity()
            }
        }
    }


    private fun initShareFlow() {
        lifecycleScope.launch {
            mviViewModel.newFetchDataShareFlow.collect {
                Log.d(TAG, "collect1 it=$it")
                bindDescView(it)
            }
        }
        lifecycleScope.launch {
            mviViewModel.newFetchDataShareFlow.collect {
                Log.d(TAG, "collect2 it=$it")
            }
        }
    }

    private fun initCommonFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //todo 普通冷流
                mviViewModel.newFetchData()
                    .onStart {
                        loading.visibility = View.VISIBLE
                    }.onCompletion {
                        loading.visibility = View.GONE
                    }.collect {
                        bindDescView(it)
                    }
            }
        }
    }

    private fun bindDescView(it: DecsEntity?) {
        tv_title.text = it!!.detail.title
        tv_score.text = it.detail.score
        tv_year.text = it.detail.year
        tv_desc.text = it.detail.desc
    }


    private val mviViewModel by lazy {
        ViewModelProvider(this)[MviViewModel2::class.java]
    }
}