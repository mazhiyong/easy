package com.lairui.easy.ui.module1.adapter

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

class NewsListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_news_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mTitle.text = item["title"]!!.toString() + ""
        viewHolder.mContent.text = item["content"]!!.toString() + ""
        GlideUtils.loadRoundCircleImage(mContext!!,item["url"]!!.toString() + "",viewHolder.mImage)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var mContent: TextView = itemView.findViewById(R.id.tvContent)
        var mImage: ImageView = itemView.findViewById(R.id.ivImage)
        var mContentLay: LinearLayout = itemView.findViewById(R.id.content_lay)

    }


}
