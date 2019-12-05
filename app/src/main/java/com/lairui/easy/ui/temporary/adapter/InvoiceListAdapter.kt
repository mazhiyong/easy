package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class InvoiceListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_invoice_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.mTvCode!!.text = item["fp_code"]!!.toString() + ""
        viewHolder.mTvNumber!!.text = item["fp_number"]!!.toString() + ""
        viewHolder.mTvMoney!!.text = item["fp_money"]!!.toString() + "元"
        viewHolder.mTvDate!!.text = item["fp_date"]!!.toString() + ""


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_code)
        lateinit var mTvCode: TextView
        @BindView(R.id.tv_number)
        lateinit var mTvNumber: TextView
        @BindView(R.id.tv_money)
        lateinit var mTvMoney: TextView
        @BindView(R.id.tv_date)
        lateinit var mTvDate: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
