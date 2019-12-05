package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener


class TradeDialogAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<TradeDialogAdapter.MyHolder>() {


    var selectItme = 0
    var onItemClickListener: OnMyItemClickListener? = null

    override fun getItemCount(): Int {
        return datas!!.size
    }


    override// 填充onCreateViewHolder方法返回的holder中的控件
    fun onBindViewHolder(holder: MyHolder, position: Int) {
        val map = datas!![position]
        holder.mTypeTv.text = map["name"]!!.toString() + ""

        if (selectItme == position) {
            holder.mTypeTv.isSelected = true
        } else {
            holder.mTypeTv.isSelected = false
        }

        holder.mTypeTv.setOnClickListener { v ->
            if (onItemClickListener != null) {
                onItemClickListener!!.OnMyItemClickListener(v, position)
                selectItme = position
                notifyDataSetChanged()
            }
        }
    }

    override// 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        // 填充布局

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_trade_dialog, null)
        return MyHolder(view)
    }

    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTypeTv: TextView

        init {
            mTypeTv = view.findViewById(R.id.type_tv)
        }
    }


}