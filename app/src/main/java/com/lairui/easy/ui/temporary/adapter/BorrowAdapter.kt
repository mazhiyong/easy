package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BorrowDetailActivity
import com.lairui.easy.ui.temporary.activity.UploadDkYongTActivity
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class BorrowAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_borrow_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）
        var ss = item["loanstate"]!!.toString() + ""
        if (UtilTools.empty(ss)) {
            ss = "0"
        }


        val status = Integer.valueOf(ss)
        var statusStr = ""
        when (status) {
            1 -> {
                statusStr = "放款中"
                viewHolder.mUploadLay!!.visibility = View.GONE
            }
            2 -> {
                statusStr = "已放款"
                viewHolder.mUploadLay!!.visibility = View.VISIBLE
                viewHolder.mUploadTv!!.setOnClickListener {
                    val intent = Intent(mContext, UploadDkYongTActivity::class.java)
                    intent.putExtra("DATA", item as Serializable)
                    mContext!!.startActivity(intent)
                }
            }
            3 -> {
                viewHolder.mUploadLay!!.visibility = View.GONE
                statusStr = "已结清"
            }
            4 -> {
                viewHolder.mUploadLay!!.visibility = View.GONE
                statusStr = "已驳回"
            }
            else -> {
                viewHolder.mUploadLay!!.visibility = View.GONE
                statusStr = ""
            }
        }

        val list = SelectDataUtil.getNameCodeByType("loanState")
        val mm = SelectDataUtil.getMapByKey(ss + "", list)
        statusStr = mm[ss + ""]!!.toString() + ""


        viewHolder.mStatusTv!!.text = statusStr
        viewHolder.mBankNameTv!!.text = item["zifangnme"]!!.toString() + ""
        viewHolder.mTimeTv!!.text = item["flowdate"]!!.toString() + ""

        val dateTime = UtilTools.getStringFromSting2(item["flowdate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        viewHolder.mTimeTv!!.text = dateTime + ""


        val money = UtilTools.getRMBMoney(item["reqmoney"]!!.toString() + "")
        viewHolder.mMoneyTv!!.text = money

        viewHolder.mItemLay!!.setOnClickListener {
            val intent = Intent(mContext, BorrowDetailActivity::class.java)
            intent.putExtra("DATA", item as Serializable)
            mContext!!.startActivity(intent)
        }
    }

    //{
    //		"jixishum": "2",
    //		"reqmoney": "100000",
    //		"flowdate": "20171019",
    //		"loansqid": "1729200000092879",
    //		"loancode": "L07",
    //		"zifangnme": "廊坊银行股份有限公司营业部",
    //		"loanstate": "3",
    //		"stopdate": "20181014",
    //		"zifangbho": "BOLF8888"
    //	}                                             借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.time_tv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.status_tv)
        lateinit var mStatusTv: TextView
        @BindView(R.id.bank_name_tv)
        lateinit var mBankNameTv: TextView
        @BindView(R.id.upload_tv)
        lateinit var mUploadTv: TextView
        @BindView(R.id.upload_lay)
        lateinit var mUploadLay: LinearLayout
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
