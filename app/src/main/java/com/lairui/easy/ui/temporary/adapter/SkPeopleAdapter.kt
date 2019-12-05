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

class SkPeopleAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_sk_people, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        val accname = item["accname"]!!.toString() + ""
        val bankname = item["bankname"]!!.toString() + ""
        val accid = item["accid"]!!.toString() + ""


        viewHolder.mNameValueTv!!.text = accname
        viewHolder.mBankNameTv!!.text = bankname
        viewHolder.mCardNumTv!!.text = accid

    }

    //"bankid":"3002",
    //"crossmark":"2",
    //"accid":"6224101646431619",
    //"wdcode":"503100000023",
    //"bankname":"南洋商业银行",
    //"wdname":"南洋商业银行（中国）有限公司北京建国门支行",
    //"accname":"嗯……在",
    //"acctype":"2"
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.name_value_tv)
        lateinit var mNameValueTv: TextView
        @BindView(R.id.bank_name_tv)
        lateinit var mBankNameTv: TextView
        @BindView(R.id.card_num_tv)
        lateinit var mCardNumTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
