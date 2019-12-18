package com.lairui.easy.ui.module2.adapter

import android.content.Context
import android.content.Intent

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
import com.lairui.easy.ui.module2.activity.CoinInfoActivity
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import com.lairui.easy.utils.imageload.GlideUtils
import java.io.Serializable

class SearchListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_search_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mTitle.text = item["jc"]!!.toString() + ""
        viewHolder.mContent.text = item["code"]!!.toString() + ""
        viewHolder.mTimeTv.text = item["name"]!!.toString() + ""

        viewHolder.contentLay.setOnClickListener {
            val intent = Intent(mContext,CoinInfoActivity::class.java)
            item["code"] = item["jc"].toString()+item["code"].toString()
            intent.putExtra("DATA", item as Serializable)
            mContext!!.startActivity(intent)


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
        lateinit var contentLay:LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
