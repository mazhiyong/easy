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
import com.lairui.easy.ui.temporary.activity.RepaymentInfoActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class RepaymentAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_repayment_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        val money = UtilTools.getRMBMoney(item["backbejn"]!!.toString() + "")
        val time = UtilTools.getStringFromSting2(item["creatime"]!!.toString() + "", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")

        viewHolder.mBankNameTv!!.text = item["zifangnme"]!!.toString() + ""
        viewHolder.mTimeTv!!.text = time
        viewHolder.mMoneyTv!!.text = money

        viewHolder.mItemLay!!.setOnClickListener {
            val intent = Intent(mContext, RepaymentInfoActivity::class.java)
            intent.putExtra("DATA", item as Serializable)
            mContext!!.startActivity(intent)
        }
    }

    //{
    //		"backlixi": "1917",
    //		"repaypath": "",
    //		"backtype": "2",
    //		"creatime": "20160523093847",
    //		"rtnbillid": "1614400000009031",
    //		"zifangnme": "廊坊银行股份有限公司北环路支行",
    //		"checkstate": "2",
    //		"crhkdtme": "20160523093847",
    //		"backbejn": "300000"
    //	}                                             借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.time_tv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.bank_name_tv)
        lateinit var mBankNameTv: TextView
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
