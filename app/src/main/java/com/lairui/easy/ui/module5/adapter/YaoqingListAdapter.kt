package com.lairui.easy.ui.module5.adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R

import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter

class YaoqingListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_yaoqing_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder


        viewHolder.nameTv.text = item["name"]!!.toString() + ""
        viewHolder.phoneTv.text = item["phone"]!!.toString() + ""
        viewHolder.ipTv.text = item["ip"]!!.toString() + ""
        viewHolder.timeTv.text = item["time"]!!.toString() + ""


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTv: TextView = itemView.findViewById(R.id.nameTv)

        var phoneTv: TextView = itemView.findViewById(R.id.phoneTv)

        var timeTv: TextView = itemView.findViewById(R.id.timeTv)

        var ipTv: TextView = itemView.findViewById(R.id.ipTv)



        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
