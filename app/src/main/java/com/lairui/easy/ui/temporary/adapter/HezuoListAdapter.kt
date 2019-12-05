package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener

import butterknife.BindView
import butterknife.ButterKnife

class HezuoListAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<HezuoListAdapter.MyHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null

    var selectItem = -1

    override fun getItemCount(): Int {
        return datas!!.size
    }

    override// 填充onCreateViewHolder方法返回的holder中的控件
    fun onBindViewHolder(holder: MyHolder, position: Int) {

        val map = datas!![position]
        holder.mItemName!!.text = map["zifangnme"]!!.toString() + ""
        holder.mLayout!!.setOnClickListener { v ->
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(v, position)
            }
            selectItem = position
            notifyDataSetChanged()
        }

        if (position == selectItem) {
            holder.mLayout!!.isSelected = true
        } else {
            holder.mLayout!!.isSelected = false
        }

    }

    override// 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        // 填充布局
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_hezuo_list, arg0, false)
        return MyHolder(view)
    }


    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.item_name)
        lateinit var mItemName: TextView
        @BindView(R.id.layout_root)
        lateinit var mLayout: LinearLayout

        init {
            ButterKnife.bind(this, view)
        }
    }

}