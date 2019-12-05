package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools

class MyAmountAdapter(context: Context) : ListBaseAdapter() {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            val map = mDataList[position]
            return if (map.containsKey("viewType")) {
                TYPE_FIRST
            } else {
                TYPE_NORMAL
            }
        } else {
            return TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_FIRST) {
            ViewHolder(mLayoutInflater.inflate(R.layout.item_my_pre_sx, parent, false), TYPE_FIRST)
        } else {
            ViewHolder(mLayoutInflater.inflate(R.layout.item_my_amount, parent, false), TYPE_NORMAL)
        }

        // return new ViewHolder(mLayoutInflater.inflate(R.layout.item_my_amount, parent, false));
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        val type = getItemViewType(position)

        when (type) {
            TYPE_NORMAL -> {
                val money = UtilTools.getRMBMoney(item["creditmoney"]!!.toString() + "")
                val useMoney = UtilTools.getRMBMoney(item["usingcredit"]!!.toString() + "")
                val dongJieMoney = UtilTools.getRMBMoney(item["frzcredit"]!!.toString() + "")
                val nianLilv = UtilTools.getlilv(item["daiklilv"]!!.toString() + "")
                val status2 = item["creditstate"]!!.toString() + ""
                val startTime = UtilTools.getStringFromSting2(item["begindate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
                val endTime = UtilTools.getStringFromSting2(item["enddate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
                viewHolder.mSxEdValueTv?.text = money
                viewHolder.mHasUseValueTv?.text = useMoney
                viewHolder.mNianLilvValueTv?.text = nianLilv
                viewHolder.mSxHtValueTv?.text = item["creditfile"]!!.toString() + ""
                viewHolder.mSxStopValueTv?.text = endTime

                viewHolder.mDongJieValueTv?.text = dongJieMoney
                viewHolder.mSxStartValueTv?.text = startTime


                when (status2) {
                    "0" -> viewHolder.mStatusTv?.text = "未开通"
                    "1" -> viewHolder.mStatusTv?.text = "已开通"
                    "2" -> viewHolder.mStatusTv?.text = "暂停"
                    "3" -> viewHolder.mStatusTv?.text = "终止"
                    "4" -> viewHolder.mStatusTv?.text = "已签署"
                }
            }
            TYPE_FIRST -> {
                val shenqingMoney = item["creditmoney"]!!.toString() + ""//申请金额
                val jiekuanQixian = item["singlelimit"]!!.toString() + ""//借款期限
                val daikuanKind = item["reqloantp"]!!.toString() + ""//贷款种类
                //Map<String,Object> daikuanKindMap = SelectDataUtil.getMap(daikuanKind,SelectDataUtil.getDaikuanType());
                val daikuanKindMap = SelectDataUtil.getMap(daikuanKind, SelectDataUtil.getNameCodeByType("loanType"))
                var shenqingDate = item["sqdate"]!!.toString() + ""//申请日期
                shenqingDate = UtilTools.getStringFromSting2(shenqingDate, "yyyyMMdd", "yyyy-MM-dd")
                viewHolder.mTitleTv?.text = "得融在线"
                val status = item["creditstate"]!!.toString() + ""
                if (status == "14") {
                    viewHolder.mStatusTv?.text = "已生效"
                } else {
                    viewHolder.mStatusTv?.text = "审核中"
                }
                viewHolder.mShenqingMoneyTv?.text = UtilTools.getRMBMoney(shenqingMoney)
                viewHolder.mJiekuanQixianTv?.text = jiekuanQixian + "月"
                viewHolder.mDaikuanKindTv?.text = daikuanKindMap["name"]!!.toString() + ""
                viewHolder.mShenqingDateTv?.text = shenqingDate
            }
        }


    }


    inner class ViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {

        internal var mTitleTv: TextView? = null
        internal var mShenqingMoneyTv: TextView? = null
        internal var mJiekuanQixianTv: TextView? = null
        internal var mDaikuanKindTv: TextView? = null
        internal var mShenqingDateTv: TextView? = null
        //--------
        internal var mStatusTv: TextView? = null
        internal var mSxEdValueTv: TextView? = null
        internal var mHasUseValueTv: TextView? = null
        internal var mDongJieValueTv: TextView? = null
        internal var mNianLilvValueTv: TextView? = null
        internal var mSxHtValueTv: TextView? = null
        internal var mSxStartValueTv: TextView? = null
        internal var mSxStopValueTv: TextView? = null

        init {
            if (type == TYPE_FIRST) {
                mTitleTv = itemView.findViewById(R.id.title_tv)
                mStatusTv = itemView.findViewById(R.id.status_tv)
                mShenqingMoneyTv = itemView.findViewById(R.id.shenqing_money_tv)
                mJiekuanQixianTv = itemView.findViewById(R.id.jiekuan_qixian_tv)
                mDaikuanKindTv = itemView.findViewById(R.id.daikuan_kind_tv)
                mShenqingDateTv = itemView.findViewById(R.id.shenqing_date_tv)
            } else {
                mStatusTv = itemView.findViewById(R.id.status_tv)
                mSxEdValueTv = itemView.findViewById(R.id.sx_ed_value_tv)
                mHasUseValueTv = itemView.findViewById(R.id.has_use_value_tv)
                mDongJieValueTv = itemView.findViewById(R.id.dongjie_money_tv)
                mNianLilvValueTv = itemView.findViewById(R.id.nian_lilv_value_tv)
                mSxHtValueTv = itemView.findViewById(R.id.sx_ht_value_tv)
                mSxStartValueTv = itemView.findViewById(R.id.sx_start_value_tv)
                mSxStopValueTv = itemView.findViewById(R.id.sx_stop_value_tv)
            }

        }
    }

    companion object {

        val TYPE_NORMAL = 1
        val TYPE_FIRST = 0
    }


}
