package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R

import butterknife.BindView
import butterknife.ButterKnife

class SamePeopleAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_same_people, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        val idno = item["idno"]!!.toString() + ""
        val relation = item["relation"]!!.toString() + ""
        val name = item["name"]!!.toString() + ""


        viewHolder.mNameValutTv!!.text = name
        viewHolder.mGxValueTv!!.text = "($relation)"
        viewHolder.mIdcardValueTv!!.text = idno

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.name_valut_tv)
        lateinit var mNameValutTv: TextView
        @BindView(R.id.gx_value_tv)
        lateinit var mGxValueTv: TextView
        @BindView(R.id.idcard_value_tv)
        lateinit var mIdcardValueTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
