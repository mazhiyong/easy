package com.lairui.easy.ui.temporary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class MyBorrowMoneyAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_borrow_mymoney_list, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder


        viewHolder.mTypeTv!!.text = item["type"]!!.toString() + ""
        viewHolder.mStateTv!!.text = item["state"]!!.toString() + ""
        viewHolder.mDateTv!!.text = item["date"]!!.toString() + ""
        viewHolder.mMoneyTv!!.text = item["money"]!!.toString() + ""
        viewHolder.mRateTv!!.text = item["rate"]!!.toString() + ""
        viewHolder.mQixianTv!!.text = item["qixian"]!!.toString() + ""
        viewHolder.mNumberTv!!.text = item["number"]!!.toString() + ""
        viewHolder.mPeopleTv!!.text = item["peopel"]!!.toString() + ""


        /* String type = item.get("creditcd") + "";
        switch (type) {
            case "L03":
                viewHolder.mTypeTv.setText("应收账款池");
                break;
            case "L11":
                viewHolder.mTypeTv.setText("信用融资");
                break;
            default:
                viewHolder.mTypeTv.setText("未知");
                break;
        }*/



        viewHolder.mUploadTv!!.setOnClickListener { }

        viewHolder.mPlanTv!!.setOnClickListener { }

        viewHolder.mHuankuanTv!!.setOnClickListener { }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.type_tv)
        lateinit var mTypeTv: TextView
        @BindView(R.id.state_tv)
        lateinit var mStateTv: TextView
        @BindView(R.id.date_tv)
        lateinit var mDateTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.rate_tv)
        lateinit var mRateTv: TextView
        @BindView(R.id.qixian_tv)
        lateinit var mQixianTv: TextView
        @BindView(R.id.number_tv)
        lateinit var mNumberTv: TextView
        @BindView(R.id.people_tv)
        lateinit var mPeopleTv: TextView
        @BindView(R.id.upload_tv)
        lateinit var mUploadTv: TextView
        @BindView(R.id.plan_tv)
        lateinit var mPlanTv: TextView
        @BindView(R.id.huankuan_tv)
        lateinit var mHuankuanTv: TextView
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView
        @BindView(R.id.linearLay)
        lateinit var mLinearLay: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
