package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class PayTheAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_pay_the_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as PayTheAdapter.ViewHolder


        val ss = item["amt"]!!.toString() + ""
        val money = UtilTools.getRMBMoney(ss)

        viewHolder.mBankNameTv!!.text = item["opnbnknm"]!!.toString() + ""
        viewHolder.mNameTv!!.text = item["accnm"]!!.toString() + ""
        viewHolder.mBankNumTv!!.text = UtilTools.getIDXing(item["accno"]!!.toString() + "")
        viewHolder.mMoneyTv!!.text = money


    }

    //	"loansqid": "1729200000092879",
    //	"accnm": "钢铁侠核心企业",
    //	"paysta": "1",
    //	"accno": "7894124512465214514",
    //	"opnbnkid": "104",
    //	"amt": "100000",
    //	"stflg": "2",
    //	"opnbnknm": "中国银行"

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.name_tv)
        lateinit var mNameTv: TextView
        @BindView(R.id.bank_name_tv)
        lateinit var mBankNameTv: TextView
        @BindView(R.id.bank_num_tv)
        lateinit var mBankNumTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}