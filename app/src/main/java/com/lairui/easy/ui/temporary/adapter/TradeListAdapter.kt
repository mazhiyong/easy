package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.TradeDetailActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife

class TradeListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_trade, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder
        //交易类型（top_up：充值，withdraw：提现，borrow：借款，repayment：还款，other：其他）
        val type = item["billtype"]!!.toString() + ""
        var typeShow = ""
        when (type) {
            "top_up" -> typeShow = "充值"
            "withdraw" -> typeShow = "提现"
            "borrow" -> typeShow = "借款"
            "repayment" -> typeShow = "还款"
            "other" -> typeShow = "其他"
        }
        viewHolder.mTradeTypeTv!!.text = typeShow


        var time = item["dealtime"]!!.toString() + ""
        time = UtilTools.getStringFromSting2(time, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")

        viewHolder.mTimeTv!!.text = time
        //viewHolder.mStatusTv.setText(item.get("abstract")+"");
        /*if ((item.get("amt")+"").contains("-")){
            viewHolder.mMoneyTv.setText(UtilTools.getMoney(item.get("amt")+""));
        }else {
            viewHolder.mMoneyTv.setText("+"+UtilTools.getMoney(item.get("amt")+""));
        }*/

        viewHolder.mMoneyTv!!.text = UtilTools.getRMBMoneyZF(item["amt"]!!.toString() + "")



        viewHolder.mTradeLay!!.setOnClickListener {
            val intent = Intent(mContext, TradeDetailActivity::class.java)
            intent.putExtra("DATA", item as Serializable)
            mContext!!.startActivity(intent)
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.trade_type_tv)
        lateinit var mTradeTypeTv: TextView
        @BindView(R.id.time_tv)
        lateinit var mTimeTv: TextView
        /*  @BindView(R.id.status_tv)
        TextView mStatusTv;*/
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.trade_lay)
        lateinit var mTradeLay: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
