package com.lairui.easy.ui.temporary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BorrowMoneyActivity
import com.lairui.easy.ui.temporary.activity.SelectFukuanFangActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class BorrowSelectAdapter(context: Context) : ListBaseAdapter() {

    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_borrow_select_list, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder
        val type = item["creditcd"]!!.toString() + ""
        when (type) {
            "L03" -> viewHolder.mTypeTv!!.text = "应收账款池"
            "L11" -> viewHolder.mTypeTv!!.text = "信用融资"
            else -> viewHolder.mTypeTv!!.text = "未知"
        }

        val money = UtilTools.getRMBMoney(item["leftmoney"]!!.toString() + "")
        viewHolder.mAmountTv!!.text = money
        viewHolder.mRateTv!!.text = UtilTools.getlilv(item["daiklilv"]!!.toString() + "")
        viewHolder.mBorrowTv!!.text = item["zifangnme"]!!.toString() + ""
        viewHolder.mGoBorrow!!.setOnClickListener {
            val intent: Intent
            when (type) {
                "L03" -> {
                    //多个付款方
                    intent = Intent(mContext, SelectFukuanFangActivity::class.java)
                    intent.putExtra("DATA", item as Serializable)
                    mContext!!.startActivity(intent)
                }
                "L11" -> {
                    intent = Intent(mContext, BorrowMoneyActivity::class.java)
                    intent.putExtra("DATA", item as Serializable)
                    mContext!!.startActivity(intent)
                }
                else -> {
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.type_tv)
        lateinit var mTypeTv: TextView
        @BindView(R.id.amount_tv)
        lateinit var mAmountTv: TextView
        @BindView(R.id.rate_tv)
        lateinit var mRateTv: TextView
        @BindView(R.id.borrow_tv)
        lateinit var mBorrowTv: TextView
        @BindView(R.id.go_borrow)
        lateinit var mGoBorrow: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
