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

class JiangLiListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_jiangli_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mTitle.text = item["name"]!!.toString() + ""
        viewHolder.mContent.text = item["number"]!!.toString() + "元"
        viewHolder.mTimeTv.text = item["time"]!!.toString() + ""

        viewHolder.mContentLay.setOnClickListener {

        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tvTitle)
        lateinit var mTitle: TextView
        @BindView(R.id.tvContent)
        lateinit var mContent: TextView
        @BindView(R.id.timeTv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.content_lay)
        lateinit var mContentLay: LinearLayout



        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
