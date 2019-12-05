package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.ApplyAmountActivity
import com.lairui.easy.ui.temporary.activity.BorrowDetailActivity
import com.lairui.easy.ui.temporary.activity.HuankuanActivity
import com.lairui.easy.ui.temporary.activity.PeopleCheckActivity
import com.lairui.easy.ui.temporary.activity.SignLoanActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife

class WaitDoWorkAdapter(context: Context) : ListBaseAdapter() {
    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.wait_do_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val data = mDataList[position]
        // 1 预授信回退列表  2授信签署列表 3借款进度列表 4待还款列表  5共同借款人审核列表
        val type = data["type"] as String?
        var tvType = ""
        var tvDate = ""
        var tvMoney = ""
        var tvBank_or_Date = ""
        var tvStatus = ""
        when (type) {
            "1"//预授信回退列表
            -> {
                holder.mImageView!!.setImageResource(R.drawable.shenqing)
                /*  tvType=data.get("");
                 tvDate=data.get("");
                 tvMoney=data.get("");
                 tvBank_or_Date=data.get("");
                 tvDate=data.get("");*/
                tvType = "授信"

                tvDate = data["sqdate"]!!.toString() + ""
                //tvDate = UtilTools.getStringFromSting2(tvDate,"")
                tvMoney = UtilTools.getRMBMoney(data["creditmoney"]!!.toString() + "")
                tvBank_or_Date = data["zifangnme"]!!.toString() + ""
                tvStatus = "去授信"
                viewHolder.mDateOrBankTv!!.visibility = View.VISIBLE
            }
            "2"//授信签署列表
            -> {
                holder.mImageView!!.setImageResource(R.drawable.renzheng)
                tvType = "签署"
                tvDate = data["flowdate"]!!.toString() + ""
                //tvDate = UtilTools.getStringFromSting2(tvDate,"")
                tvMoney = UtilTools.getRMBMoney(data["creditmoney"]!!.toString() + "")
                tvBank_or_Date = data["zifangnme"]!!.toString() + ""
                val status = data["qsstate"]!!.toString() + ""
                if (status == "0") {
                    tvStatus = "去签署"
                } else if (status == "1") {
                    tvStatus = "处理中"
                }
                viewHolder.mDateOrBankTv!!.visibility = View.VISIBLE
            }
            "3"//借款进度列表
            -> {
                holder.mImageView!!.setImageResource(R.drawable.bromoney)
                tvType = "借款"
                tvDate = data["flowdate"]!!.toString() + ""
                //tvDate = UtilTools.getStringFromSting2(tvDate,"")
                tvMoney = UtilTools.getRMBMoney(data["reqmoney"]!!.toString() + "")
                tvBank_or_Date = data["loanstepdesc"]!!.toString() + ""
                tvStatus = "放款审核中"
                viewHolder.mDateOrBankTv!!.visibility = View.GONE
            }
            "4"//待还款列表
            -> {
                holder.mImageView!!.setImageResource(R.drawable.paymoney)
                tvType = "还款"
                tvDate = data["repaydate"]!!.toString() + ""
                //tvDate = UtilTools.getStringFromSting2(tvDate,"")
                tvMoney = UtilTools.getRMBMoney(data["repayamt"]!!.toString() + "")
                var dateStr = data["repaydate"]!!.toString() + ""
                dateStr = "截止还款日" + UtilTools.getStringFromSting2(dateStr, "yyyyMMdd", "yyyy年MM月dd日")
                tvBank_or_Date = dateStr
                tvStatus = "去还款"
                viewHolder.mDateOrBankTv!!.visibility = View.VISIBLE
            }

            "5"//共同借款人审核列表
            -> {
                holder.mImageView!!.setImageResource(R.drawable.qita)
                tvType = "审核"
                tvDate = ""
                //tvDate = UtilTools.getStringFromSting2(tvDate,"")
                tvMoney = UtilTools.getRMBMoney(data["creditmoney"]!!.toString() + "")
                tvBank_or_Date = data["firmname"]!!.toString() + ""
                tvStatus = "待审核"
                viewHolder.mDateOrBankTv!!.visibility = View.VISIBLE
            }
        }
        viewHolder.mTypeTv!!.text = tvType
        viewHolder.mDateTv!!.text = tvDate
        viewHolder.mMoneyTv!!.text = tvMoney
        viewHolder.mDateOrBankTv!!.text = tvBank_or_Date
        viewHolder.mStatusTv!!.text = tvStatus

        viewHolder.mItemLay!!.setOnClickListener {
            //根据列表类型进行不同的处理操作
            // 1 预授信回退列表  2授信签署列表 3借款进度列表 4待还款列表  5共同借款人审核列表
            val intent: Intent
            when (type) {
                "1" -> {
                    intent = Intent(mContext, ApplyAmountActivity::class.java)
                    intent.putExtra("TYPE", 1)
                    intent.putExtra("precreid", data["precreid"]!!.toString() + "")
                    mContext!!.startActivity(intent)
                }
                "2" -> {
                    intent = Intent(mContext, SignLoanActivity::class.java)
                    intent.putExtra("DATA", data as Serializable)
                    intent.putExtra("status", data["qsstate"]!!.toString() + "")
                    mContext!!.startActivity(intent)
                }
                "3" -> {
                    intent = Intent(mContext, BorrowDetailActivity::class.java)
                    intent.putExtra("DATA", data as Serializable)
                    mContext!!.startActivity(intent)
                }
                "4" -> {
                    intent = Intent(mContext, HuankuanActivity::class.java)
                    intent.putExtra("DATA", data as Serializable)
                    mContext!!.startActivity(intent)
                }
                "5" -> {
                    intent = Intent(mContext, PeopleCheckActivity::class.java)
                    intent.putExtra("DATA", data as Serializable)
                    mContext!!.startActivity(intent)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.iv_type)
        lateinit var mImageView: ImageView
        @BindView(R.id.tv_tvpe)
        lateinit var mTypeTv: TextView
        @BindView(R.id.tv_date)
        lateinit var mDateTv: TextView
        @BindView(R.id.tv_money)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.tv_bank_or_date)
        lateinit var mDateOrBankTv: TextView
        @BindView(R.id.status_tv)
        lateinit var mStatusTv: TextView
        @BindView(R.id.item_waitdo)
        lateinit var mItemLay: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
