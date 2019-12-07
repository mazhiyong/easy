package com.lairui.easy.ui.module4.adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R

import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import com.lairui.easy.utils.imageload.GlideUtils

class RecordListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_record_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mTitle.text = item["title"]!!.toString() + ""
        viewHolder.mContent.text = item["money"]!!.toString() + ""
        viewHolder.mTimeTv.text = item["time"]!!.toString() + ""
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tvTitle)
        lateinit var mTitle: TextView
        @BindView(R.id.tvContent)
        lateinit var mContent: TextView
        @BindView(R.id.timeTv)
        lateinit var mTimeTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}