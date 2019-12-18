package com.lairui.easy.ui.module2.adapter

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
class BuyAndSellAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_buyandsell_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

      /*  val status = Integer.valueOf("1")
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
        }*/
        viewHolder.mTypeTv.text = item["type"]!!.toString()
        viewHolder.mPriceTv.text = item["price"]!!.toString()
        viewHolder.mAmountTv.text = item["amount"]!!.toString()


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var mTypeTv: TextView = itemView.findViewById(R.id.typeTv)

        var mPriceTv: TextView = itemView.findViewById(R.id.priceTv)

        var mAmountTv: TextView = itemView.findViewById(R.id.amountTv)

    }
}
