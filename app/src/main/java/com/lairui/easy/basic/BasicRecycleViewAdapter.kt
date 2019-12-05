package com.lairui.easy.basic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BasicRecycleViewAdapter(protected var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, OnLongClickListener {
    protected var inflater: LayoutInflater
    protected var defItem = -1
    private var view: View? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        view = inflater.inflate(itemRes, viewGroup, false)
        return getViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
        if (defItem != -1) {
            holder.itemView.isSelected = defItem == position
        }
        bindClickListener(holder, position)
    }

    //    public View getItemView(int position){
//
//        return ;
//    }
//设置布局文件
    protected abstract val itemRes: Int

    //获取viewholder
    protected abstract fun getViewHolder(view: View?): RecyclerView.ViewHolder

    //绑定点击监听事件
    protected abstract fun bindClickListener(viewHolder: RecyclerView.ViewHolder?, position: Int)

    var select: Int
        get() = defItem
        set(position) {
            defItem = position
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        if (onItemClickListener != null) { //注意这里使用getTag方法获取数据
            onItemClickListener!!.onItemClickListener(v, v.tag as Int)
        }
    }

    override fun onLongClick(v: View): Boolean {
        return onItemLongClickListener != null && onItemLongClickListener!!.onItemLongClickListener(v, v.tag as Int)
    }

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    /*设置点击事件*/
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /*设置长按事件*/
    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View?, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClickListener(view: View?, position: Int): Boolean
    }

    init {
        inflater = LayoutInflater.from(mContext)
    }
}