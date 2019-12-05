package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.PDFLookActivity
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class PayHistoryAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_pay_history, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        val status = item["checkstate"]!!.toString() + ""
        var statusStr = ""
        when (status) {
            "0" -> statusStr = "初始化"
            "1" -> statusStr = "已申请"
            "2" -> statusStr = "还款成功"
            "3" -> statusStr = "还款失败"
        }

        val list = SelectDataUtil.getNameCodeByType("repayState")
        val mm = SelectDataUtil.getMapByKey(status + "", list)
        statusStr = mm[status + ""]!!.toString() + ""


        val backType = item["backtype"]!!.toString() + ""
        var backTypeStr = ""
        //还款账户类型(1：客户结算账户还款;2：客户资金账户还款;3：核心企业资金账户代还;4：借款客户网银还款;5：核心企业网银代
        when (backType) {
            "1" -> backTypeStr = "客户结算账户还款"
            "2" -> backTypeStr = "客户资金账户还款"
            "3" -> backTypeStr = "核心企业资金账户代还"
            "4" -> backTypeStr = "借款客户网银还款"
            "5" -> backTypeStr = "核心企业网银代"
        }

        val list2 = SelectDataUtil.getNameCodeByType("repayAcct")
        val mm2 = SelectDataUtil.getMapByKey(backType + "", list2)
        backTypeStr = mm2[backType + ""]!!.toString() + ""


        val benjin = item["backbejn"]!!.toString() + ""
        val benjin2 = UtilTools.getRMBMoney(benjin)

        val backlixi = item["backlixi"]!!.toString() + ""
        val backlixi2 = UtilTools.getRMBMoney(backlixi)


        viewHolder.mStatusTv!!.text = statusStr
        viewHolder.mTimeTv!!.text = item["creatime"]!!.toString() + ""
        viewHolder.mMoneyTv!!.text = ""
        viewHolder.mPayType!!.text = backTypeStr
        viewHolder.mPayBenjin!!.text = benjin2
        viewHolder.mPayLixi!!.text = backlixi2
        viewHolder.mPayNum!!.text = item["rtnbillid"]!!.toString() + ""

        val date = item["creatime"]!!.toString() + ""
        val d = UtilTools.getDateFromString(date, "yyyyMMddHHmmss")
        val s = UtilTools.getStringFromDate(d, "yyyy-MM-dd HH:mm:ss")
        viewHolder.mTimeTv!!.text = s

        viewHolder.mXiyiTv!!.setOnClickListener {
            val intent = Intent(mContext, PDFLookActivity::class.java)
            intent.putExtra("id", item["repaypath"]!!.toString() + "")
            mContext!!.startActivity(intent)
        }


        val tip = mContext!!.resources.getString(R.string.look_pay_book)
        var dian = tip.length
        if (tip.contains("《")) {
            dian = tip.indexOf("《")
        } else {
            dian = tip.length
        }

        /* 用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
        Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)*/
        val ss = SpannableString(tip)
        // ss.setSpan(new BankOpenXieyiActivity.TextSpanClick(false), dian, tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(mContext!!, R.color.black)), 0, dian, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(mContext!!, R.color.blue1)), dian, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewHolder.mXiyiTv!!.text = ss
        //添加点击事件时，必须设置
        viewHolder.mXiyiTv!!.movementMethod = LinkMovementMethod.getInstance()
    }


    //[{
    //	"backlixi": "1000",
    //	"repaypath": "",
    //	"backtype": "1",
    //	"creatime": "20171127160349",
    //	"rtnbillid": "1733400000109740",
    //	"zifangnme": "廊坊银行股份有限公司营业部",
    //	"checkstate": "2",  还款状态(0:初始化,1:已申请,2:还款成功,3:还款失败
    //	"crhkdtme": "20171127170102",
    //	"backbejn": "100000"
    //}]
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.status_tv)
        lateinit var mStatusTv: TextView
        @BindView(R.id.time_tv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.pay_benjin)
        lateinit var mPayBenjin: TextView
        @BindView(R.id.pay_lixi)
        lateinit var mPayLixi: TextView
        @BindView(R.id.fa_xi)
        lateinit var mFaXi: TextView
        @BindView(R.id.pay_type)
        lateinit var mPayType: TextView
        @BindView(R.id.pay_num)
        lateinit var mPayNum: TextView
        @BindView(R.id.xieyi_tv)
        lateinit var mXiyiTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}