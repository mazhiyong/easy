package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BorrowDetailActivity
import com.lairui.easy.ui.temporary.activity.HuankuanActivity
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.SwipeMenuView

import java.io.Serializable

import butterknife.BindView
import butterknife.ButterKnife

class ServiceRemindInfoAdapter(context: Context) : ListBaseAdapter() {
    val mLayoutInflater: LayoutInflater

    var mOnSwipeListener: SwipeMenuAdapter.onSwipeListener? = null
    //滑动效果按钮的监听
    interface onSwipeListener {
        //删除
        fun onDel(pos: Int)

        //置顶
        fun onTop(pos: Int)
    }

    fun setOnSwipeListener(onSwipeListener: SwipeMenuAdapter.onSwipeListener) {
        mOnSwipeListener = onSwipeListener
    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHonlder(mLayoutInflater.inflate(R.layout.service_remind_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHonlder
        val data = mDataList[position]
        // 1 预授信回退列表  2授信签署列表 3借款进度列表 4待还款列表  5共同借款人审核列表
        val type = data["type"] as String?
        var tvType = ""
        var tvDate = ""
        var tvMoney = ""
        var tvBank_or_Date = ""
        var tvStatus = ""
        when (type) {
            "1" -> {
                viewHolder.mImageView!!.setImageResource(R.drawable.shenqing)
                /*  tvType=data.get("");
                 tvDate=data.get("");
                 tvMoney=data.get("");
                 tvBank_or_Date=data.get("");
                 tvDate=data.get("");*/
                tvType = "授信"
                tvDate = "2018-09-19 14:00"
                tvMoney = UtilTools.getRMBMoney("100000000")
                tvBank_or_Date = "郑州银行金水区支行"
                tvStatus = "去授信"
            }
            "2" -> {
                viewHolder.mImageView!!.setImageResource(R.drawable.renzheng)
                tvType = "签署"
                tvDate = "2018-09-19 14:00"
                tvMoney = UtilTools.getRMBMoney("100000000")
                tvBank_or_Date = "郑州银行金水区支行"
                tvStatus = "去签署"
            }
            "3" -> {
                viewHolder.mImageView!!.setImageResource(R.drawable.bromoney)
                tvType = "借款"
                tvDate = "2018-09-19 14:00"
                tvMoney = UtilTools.getRMBMoney("100000000")
                tvBank_or_Date = "郑州银行金水区支行"
                tvStatus = "借款中"
            }
            "4" -> {
                viewHolder.mImageView!!.setImageResource(R.drawable.paymoney)
                tvType = "还款"
                tvDate = "2018-09-19 14:00"
                tvMoney = UtilTools.getRMBMoney("100000000")
                tvBank_or_Date = "4月25日应还金额"
                tvStatus = "去还款"
            }

            "5" -> {
                viewHolder.mImageView!!.setImageResource(R.drawable.qita)
                tvType = "审核"
                tvDate = "2018-09-19 14:00"
                tvMoney = UtilTools.getRMBMoney("100000000")
                tvBank_or_Date = "郑州银行金水区支行"
                tvStatus = "待审核"
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
                }
                "2" -> {
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
                }
            }
        }
        //滑动删除
        (viewHolder.itemView as SwipeMenuView).setIos(true).isLeftSwipe = true
        viewHolder.btDelete!!.setOnClickListener {
            if (null != mOnSwipeListener) {
                mOnSwipeListener!!.onDel(position)

            }
        }

    }

    internal inner class ViewHonlder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
        @BindView(R.id.bt_Delete)
        lateinit var btDelete: Button

        init {
            ButterKnife.bind(this, itemView)
        }
    }

}
