package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

import java.util.ArrayList

open class ListBaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented")
    }

    protected var mContext: Context? = null

    protected var mDataList: MutableList<MutableMap<String, Any>> = ArrayList()

    val dataList: List<MutableMap<String, Any>>
        get() = mDataList

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    fun setDataList(list: Collection<MutableMap<String, Any>>) {
        this.mDataList.clear()
        this.mDataList.addAll(list)
        notifyDataSetChanged()
    }

    fun addAll(list: Collection<MutableMap<String, Any>>) {
        val lastIndex = this.mDataList.size
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size)
        }
    }

    fun delete(position: Int) {
        mDataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        mDataList.clear()
        //  notifyDataSetChanged();
    }


}
