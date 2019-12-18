package com.lairui.easy.ui.module4.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.lairui.easy.R

import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.ui.module2.activity.BuyAndSellActivity
import com.lairui.easy.ui.module4.activity.CeLueItemCurrentActivity
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
       /* var ss = item["status"]!!.toString() + ""
        if (UtilTools.empty(ss)) {
            ss = "1"
        }*/
        val status = Integer.valueOf("1")
        when (status) {
            1 -> {
                viewHolder.mStatusTv.text = "操盘中"
                viewHolder.mStatusTv.setTextColor(ContextCompat.getColor(mContext!!, R.color.font_c))
                viewHolder.mTradeIv.visibility = View.VISIBLE
            }
            2 -> {
                viewHolder.mStatusTv.text = "已结清"
                viewHolder.mStatusTv.setTextColor(ContextCompat.getColor(mContext!!, R.color.black99))
                viewHolder.mTradeIv.visibility = View.GONE
            }
        }
        viewHolder.mTitleTv.text = item["name"]!!.toString() + "元"
        viewHolder.mTimeTv.text = item["time"]!!.toString() + "元"

        //val dateTime = UtilTools.getStringFromSting2(item["flowdate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        //viewHolder.mTimeTv!!.text = dateTime + ""

        viewHolder.mJingJieMoneyTv.text = item["warning"]!!.toString() + ""
        viewHolder.mPingCangMoneyTv.text = item["close"]!!.toString() + ""


        viewHolder.mItemIv.setOnClickListener {
            val intent = Intent(mContext,CeLueItemCurrentActivity::class.java)
            mContext!!.startActivity(intent)
        }
        viewHolder.mTradeIv.setOnClickListener {
            val intent = Intent(mContext,BuyAndSellActivity::class.java)
            intent.putExtra("mark",item["mark"].toString())
            mContext!!.startActivity(intent)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var mTimeTv: TextView = itemView.findViewById(R.id.timeTv)
        var mTitleTv: TextView =itemView.findViewById(R.id.titleTv)
        var mStatusTv: TextView = itemView.findViewById(R.id.typeTv)
        var mJingJieMoneyTv: TextView = itemView.findViewById(R.id.jingjieMoneyTv)
        var mPingCangMoneyTv: TextView = itemView.findViewById(R.id.pingcangMoneyTv)
        var mItemIv: TextView = itemView.findViewById(R.id.itemTv)
        var mTradeIv:TextView = itemView.findViewById(R.id.tradeTv)
    }
}
