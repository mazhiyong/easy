package com.lairui.easy.ui.temporary.adapter

import android.content.Context

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R

class ModifyFileAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_mofify_file_type, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.mTitleTv.text = item["name"]!!.toString() + ""

        val files = item["files"] as List<MutableMap<String, Any>>?

        if (files == null) {

        } else {
            val manager = LinearLayoutManager(mContext)
            manager.orientation = RecyclerView.VERTICAL
            viewHolder.mRecyclerView.layoutManager = manager

            val mGridImageAdapter = mContext?.let { ModifyFileDateAdapter(it, files) }
            viewHolder.mRecyclerView.adapter = mGridImageAdapter

        }

    }


    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTitleTv: TextView
        val mRecyclerView: RecyclerView

        init {
            mTitleTv = itemView.findViewById(R.id.titile)
            mRecyclerView = itemView.findViewById(R.id.list_view)

        }
    }

}
