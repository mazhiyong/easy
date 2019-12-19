package com.lairui.easy.ui.module1.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.R
import com.lairui.easy.ui.module1.activity.NoticeDetialActivity
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import java.io.Serializable

class NoticeListAdapter(context: Context) : ListBaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_notice, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Map<String, Any> = mDataList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.titleTv.text = item["name"].toString()
        viewHolder.timeTv.text = item["date"].toString()+item["time"].toString()
        viewHolder.tradeLay.setOnClickListener {
            val intent = Intent(mContext, NoticeDetialActivity::class.java)
            intent.putExtra("DATA", mDataList[position] as Serializable)
            mContext!!.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var titleTv: TextView = itemView!!.findViewById(R.id.title_tv)
        var timeTv: TextView = itemView!!.findViewById(R.id.time_tv)
        var tradeLay: CardView = itemView!!.findViewById(R.id.trade_lay)

    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}