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

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class ShouxinJkListAdapter(context: Context) : ListBaseAdapter() {

    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_shouxin_jk, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）

        val str = item["loanstate"]!!.toString() + ""
        val status: Int
        if (UtilTools.empty(str)) {
            status = -123
        } else {
            status = Integer.valueOf(item["loanstate"]!!.toString() + "")//loanstate
        }

        val list = SelectDataUtil.getNameCodeByType("loanState")
        val mm = SelectDataUtil.getMapByKey(status.toString() + "", list)
        val statusStr = mm[status.toString() + ""]!!.toString() + ""
        /* String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "放款中";
                break;
            case 2:
                statusStr = "还款中";
                break;
            case 3:
                statusStr = "已结清";
                break;
            case 4:
                statusStr = "已驳回";
                break;
            default:
                statusStr = "";
                break;
        }*/

        viewHolder.mStatusTv!!.text = statusStr

        viewHolder.mJeikuanYueTv!!.text = UtilTools.getRMBMoney(item["reqmoney"]!!.toString() + "")

        viewHolder.mJiekuanCodeTv!!.text = item["loansqid"]!!.toString() + ""

        val dateTime = UtilTools.getStringFromSting2(item["flowdate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        viewHolder.mShengqingDateTv!!.text = dateTime + ""

        val dateTime2 = UtilTools.getStringFromSting2(item["stopdate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        viewHolder.mHuankuanStopDate!!.text = dateTime2 + ""

        val money = UtilTools.getRMBMoney(item["reqmoney"]!!.toString() + "")
        viewHolder.mMoneyTv!!.text = money

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
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.status_tv)
        lateinit var mStatusTv: TextView
        @BindView(R.id.jeikuan_yue_tv)
        lateinit var mJeikuanYueTv: TextView
        @BindView(R.id.shengqing_date_tv)
        lateinit var mShengqingDateTv: TextView
        @BindView(R.id.huankuan_stop_date)
        lateinit var mHuankuanStopDate: TextView
        @BindView(R.id.jiekuan_code_tv)
        lateinit var mJiekuanCodeTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
