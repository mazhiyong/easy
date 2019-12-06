package com.lairui.easy.ui.module4.adapter

import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BorrowDetailActivity
import com.lairui.easy.ui.temporary.activity.UploadDkYongTActivity
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter

/**
 */
class TradeListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_trade_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        //状态（1：操盘中 2：已结清 ）
        var ss = item["status"]!!.toString() + ""
        if (UtilTools.empty(ss)) {
            ss = "1"
        }
        val status = Integer.valueOf(ss)
        when (status) {
            1 -> {
                viewHolder.mStatusTv.text = "操盘中"
                viewHolder.mStatusTv.setTextColor(ContextCompat.getColor(mContext!!, R.color.font_c))
                viewHolder.mTradeIv!!.visibility = View.VISIBLE
            }
            2 -> {
                viewHolder.mStatusTv.text = "已结清"
                viewHolder.mStatusTv.setTextColor(ContextCompat.getColor(mContext!!, R.color.black99))
                viewHolder.mTradeIv!!.visibility = View.GONE
            }
        }
        viewHolder.mTitleTv.text = item["title"]!!.toString() + ""
        viewHolder.mTimeTv.text = item["time"]!!.toString() + ""

        //val dateTime = UtilTools.getStringFromSting2(item["flowdate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        //viewHolder.mTimeTv!!.text = dateTime + ""

        viewHolder.mJingJieMoneyTv.text = item["a"]!!.toString() + ""
        viewHolder.mPingCangMoneyTv.text = item["b"]!!.toString() + ""

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.timeTv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.titleTv)
        lateinit var mTitleTv: TextView
        @BindView(R.id.typeTv)
        lateinit var mStatusTv: TextView
        @BindView(R.id.jingjieMoneyTv)
        lateinit var mJingJieMoneyTv: TextView
        @BindView(R.id.pingcangMoneyTv)
        lateinit var mPingCangMoneyTv: TextView
        @BindView(R.id.itemTv)
        lateinit var mItemIv: TextView
        @BindView(R.id.tradeTv)
        lateinit var mTradeIv:TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
