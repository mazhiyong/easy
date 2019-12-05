package com.lairui.easy.ui.temporary.adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools

import java.util.ArrayList
import java.util.Date

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class PayPlanAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater

    var showList: MutableList<Boolean>
        get() = mShowList
        set(showList) {
            mShowList = showList
        }
    private var mShowList: MutableList<Boolean> = ArrayList()

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_pay_plan, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        val benjin = java.lang.Double.valueOf(item["bqyhbnjn"]!!.toString() + "")
        val lixi = java.lang.Double.valueOf(item["bqyhlixi"]!!.toString() + "")
        val allm = UtilTools.add(benjin, lixi)

        val endStr = item["dangqend"]!!.toString() + ""


        viewHolder.mStatusTv!!.text = "待还款"
        val endt = UtilTools.getStringFromSting2(endStr, "yyyyMMdd", "yyyy-MM-dd")
        viewHolder.mTimeTv!!.text = endt
        viewHolder.mQishuTv!!.text = (position + 1).toString() + "/" + item["huankqis"] + "期"
        viewHolder.mBenjinTv!!.text = UtilTools.getRMBMoney(item["bqyhbnjn"]!!.toString() + "")
        viewHolder.mLixiTv!!.text = UtilTools.getRMBMoney(item["bqyhlixi"]!!.toString() + "")
        viewHolder.mAllMoneyTv!!.text = UtilTools.getRMBMoney(allm.toString() + "")

        if (position == 0) {
            viewHolder.mIvRouteIcon!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.logistics_track_point))
            //viewHolder.mContentLay.setBackgroundColor(Color.WHITE);
            viewHolder.mIconTopLine!!.visibility = View.INVISIBLE
            viewHolder.mIconBottomLine!!.visibility = View.VISIBLE

        } else if (position == mDataList.size - 1) {
            viewHolder.mIvRouteIcon!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.logistics_track_point))
            //viewHolder.mContentLay.setBackgroundColor(Color.parseColor("#F4F5F5"));
            viewHolder.mIconTopLine!!.visibility = View.VISIBLE
            viewHolder.mIconBottomLine!!.visibility = View.INVISIBLE

        } else {
            viewHolder.mIvRouteIcon!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.logistics_track_point))
            //viewHolder.mContentLay.setBackgroundColor(Color.parseColor("#F4F5F5"));
            viewHolder.mIconTopLine!!.visibility = View.VISIBLE
            viewHolder.mIconBottomLine!!.visibility = View.VISIBLE
        }

        val currentMonth = UtilTools.getStringFromDate(Date(), "yyyyMM")
        if (endStr.startsWith(currentMonth)) {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, UtilTools.dip2px(mContext!!, 60))
            viewHolder.mIvRouteIcon!!.layoutParams = layoutParams
            viewHolder.mIvRouteIcon!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.current_month_icon))
        } else {
            val layoutParams = LinearLayout.LayoutParams(UtilTools.dip2px(mContext!!, 16), UtilTools.dip2px(mContext!!, 16))
            viewHolder.mIvRouteIcon!!.layoutParams = layoutParams
            viewHolder.mIvRouteIcon!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.current_green_circle))
        }

        val b = mShowList[position]
        if (b) {
            viewHolder.mShowLay!!.visibility = View.VISIBLE
            viewHolder.mArrowView!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.android_list_down))
        } else {
            viewHolder.mShowLay!!.visibility = View.GONE
            viewHolder.mArrowView!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.android_list_idex))
        }

        viewHolder.mHeadLay!!.setOnClickListener {
            if (viewHolder.mShowLay!!.isShown) {
                mShowList[position] = false
                viewHolder.mShowLay!!.visibility = View.GONE
                viewHolder.mArrowView!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.android_list_idex))
            } else {
                mShowList[position] = true
                viewHolder.mShowLay!!.visibility = View.VISIBLE
                viewHolder.mArrowView!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.android_list_down))
            }
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.icon_top_line)
        lateinit var mIconTopLine: View
        @BindView(R.id.iv_route_icon)
        lateinit var mIvRouteIcon: ImageView
        @BindView(R.id.icon_bottom_line)
        lateinit var mIconBottomLine: View
        @BindView(R.id.status)
        lateinit var mStatusTv: TextView
        @BindView(R.id.time_tv)
        lateinit var mTimeTv: TextView
        @BindView(R.id.qishu_tv)
        lateinit var mQishuTv: TextView
        @BindView(R.id.benjin_tv)
        lateinit var mBenjinTv: TextView
        @BindView(R.id.lixi_tv)
        lateinit var mLixiTv: TextView
        @BindView(R.id.content_lay)
        lateinit var mContentLay: LinearLayout
        @BindView(R.id.show_lay)
        lateinit var mShowLay: LinearLayout
        @BindView(R.id.head_lay)
        lateinit var mHeadLay: LinearLayout
        @BindView(R.id.all_money)
        lateinit var mAllMoneyTv: TextView
        @BindView(R.id.arrow_view)
        lateinit var mArrowView: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}