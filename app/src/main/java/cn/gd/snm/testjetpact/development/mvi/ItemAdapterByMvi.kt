package cn.gd.snm.testjetpact.development.mvi

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.development.mvvm.ListInfoEntity
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 将recyclerview的刷新具体逻辑放adapter中，刷新方式由Ui主动观察
 *
 */
open class ItemAdapterByMvi(var mcontext: Context, var data: ListInfoEntity,
                            var stateFlow: MutableStateFlow<ListInfoEntity>) :
    RecyclerView.Adapter<ItemAdapterByMvi.TestViewHolder>() {

    companion object{
        val TAG = this::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        var itemLay = LayoutInflater.from(mcontext).inflate(R.layout.item_view, parent,false)

        var holder = TestViewHolder(itemLay)

        //todo 添加点击后，显示选中状态
        holder.itemView.setOnClickListener{
            postNotifyPos(it.tag as Int)
        }

        return holder
    }

    /**
     * 更新指定位置，通过数据直接驱动ui刷新。
     *
     */
    private fun postNotifyPos(pos:Int){
        //记录选中状态
        data.lastSelectPos = data.selectPos
        data.selectPos = pos

        //当前非全部刷新
        data.notifyAll = false

        //当前pos设置选中，上一个选中恢复未选中。
        data.datas[data.selectPos].isSelect = true
        data.datas[data.lastSelectPos].isSelect = false

        //通知上一个状态恢复未选中。
        data.notifyPos = data.lastSelectPos
        stateFlow.value = data

        //通知当前状态选中。
        data.notifyPos = data.selectPos
        stateFlow.value = data
    }

    /**
     * 对外提供刷新列表函数,数据作为Ui的唯一刷新方式。
     *
     */
    fun update(it: ListInfoEntity){
        if(it.notifyAll){
            Log.d(TAG,"notifyAll...")
            notifyDataSetChanged()
        }else{
            Log.d(TAG,"notify specified pos, pos=${it.notifyPos}")
            notifyItemChanged(it.notifyPos)
        }
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder...")
        Glide.with(mcontext).load(data.datas[position].pic_url).into(holder.img)
        holder.tx.text = data.datas[position].title

        holder.itemView.tag = position

        // todo 通常需求中伴随着选中颜色。
        holder.tx.setTextColor(Color.BLACK)
        if(data.datas[position].isSelect){
            holder.tx.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return data.datas.size
    }

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.item_img)
        var tx: TextView = itemView.findViewById(R.id.item_tv)
    }
}