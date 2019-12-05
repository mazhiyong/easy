package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lairui.easy.R

import butterknife.BindView
import butterknife.ButterKnife

class ContcatsListAdapter(private val mContext: Context, private val mDatas: List<MutableMap<String, Any>>) : RecyclerView.Adapter<ContcatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.contact_lsit_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView!!.text = ""
        Glide.with(mContext).load("").apply(RequestOptions().placeholder(R.drawable.tip_orange).error(R.drawable.tip_orange)).into(holder.mImageView!!)

    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_contact_mes)
        lateinit var mTextView: TextView
        @BindView(R.id.iv_contact_ico)
        lateinit var mImageView: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
