package com.lairui.easy.ui.module5.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.R
import com.lairui.easy.ui.module1.activity.NoticeDetialActivity
import com.lairui.easy.ui.module5.activity.BankPayActivity
import com.lairui.easy.ui.module5.activity.WeiAndAliPayActivity
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import java.io.Serializable

class PayWayListAdapter(context: Context) : ListBaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_payway, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Map<String, Any> = mDataList[position]
        val viewHolder = holder as ViewHolder
        //        String kind = item.get("kind")+"";
//        if (kind.equals("0")){
//            viewHolder.titleTv.setBackgroundResource(R.drawable.background_corners_gray_lightest);
//        }
        when(item["type"]){
            1 ->{
                viewHolder.titleTv.text = "微信"
                viewHolder.typeIv.setBackgroundResource(R.drawable.wei_pay)
            }
            2 ->{
                viewHolder.titleTv.text = "支付宝"
                viewHolder.typeIv.setBackgroundResource(R.drawable.ali_pay)
            }
            3 ->{
                viewHolder.titleTv.text = "银行卡"
                viewHolder.typeIv.setBackgroundResource(R.drawable.bank_pay)
            }

        }

        viewHolder.tradeLay.setOnClickListener {
            if (item["type"] == 3){ //银行卡支付
                 val intent = Intent(mContext, BankPayActivity::class.java)
                 //intent.putExtra("DATA", mDataList[position] as Serializable)
                 mContext!!.startActivity(intent)
            }else{
                val intent = Intent(mContext, WeiAndAliPayActivity::class.java)
                //intent.putExtra("DATA", mDataList[position] as Serializable)
                mContext!!.startActivity(intent)
            }

        }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.titleTv)
        lateinit var titleTv: TextView
        @BindView(R.id.typeIv)
        lateinit var typeIv: ImageView
        @BindView(R.id.lay)
        lateinit var tradeLay: LinearLayout

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}