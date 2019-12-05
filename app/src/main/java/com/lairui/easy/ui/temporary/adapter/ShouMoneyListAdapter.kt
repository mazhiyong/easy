package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.ShouMoneyItemActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class ShouMoneyListAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_shoumoney_list, parent, false))
    }


    //	"flowdate": "20190425",
    //	"rcvfirmname": "高德2",
    //	"paymoney": 10000,
    //	"billid": "PZ001",
    //	"paidmny": 0,
    //	"vchtrdtype": "0",
    //	"paycustid": "1730613000013275",
    //	"flowid": 1,
    //	"payfirmname": "20171102核心企业",  购买方名称
    //	"vouchmny": 10000,
    //	"paydate": "20190511",
    //	"status": "1"
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder
        viewHolder.mNameTv!!.text = item["payfirmname"]!!.toString() + ""
        viewHolder.mNumberTv!!.text = item["billid"]!!.toString() + ""
        viewHolder.mDateTv!!.text = item["paydate"]!!.toString() + ""
        //viewHolder.mPersonTv.setText(item.get("person")+"");
        viewHolder.mPzMoneyTv!!.text = UtilTools.getRMBMoney(item["vouchmny"]!!.toString() + "")
        viewHolder.mShouMoneyTv!!.text = UtilTools.getRMBMoney(item["paidmny"]!!.toString() + "")
        viewHolder.mShouldMoneyTv!!.text = UtilTools.getRMBMoney(item["paymoney"]!!.toString() + "")

        val status = item["status"]!!.toString() + ""
        ///(1正常，2已融资，3已核销，4已到期)
        when (status) {
            "1" -> viewHolder.mStateIv!!.setImageResource(R.drawable.zhengchang)
            "2" -> viewHolder.mStateIv!!.setImageResource(R.drawable.rongzi)
            "3" -> viewHolder.mStateIv!!.setImageResource(R.drawable.hexiao)
            "4" -> viewHolder.mStateIv!!.setImageResource(R.drawable.daoqi)
        }

        viewHolder.mItemLay!!.setOnClickListener {
            val intent = Intent(mContext, ShouMoneyItemActivity::class.java)
            intent.putExtra("DATA", item as Serializable)
            mContext!!.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.name_tv)
        lateinit var mNameTv: TextView
        @BindView(R.id.state_iv)
        lateinit var mStateIv: ImageView
        @BindView(R.id.number_tv)
        lateinit var mNumberTv: TextView
        @BindView(R.id.date_tv)
        lateinit var mDateTv: TextView
        @BindView(R.id.pz_money_tv)
        lateinit var mPzMoneyTv: TextView
        @BindView(R.id.shou_money_tv)
        lateinit var mShouMoneyTv: TextView
        @BindView(R.id.should_money_tv)
        lateinit var mShouldMoneyTv: TextView
        @BindView(R.id.person_tv)
        lateinit var mPersonTv: TextView
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
