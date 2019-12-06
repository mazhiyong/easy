package com.lairui.easy.ui.module1.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.R
import com.lairui.easy.ui.module1.activity.NoticeDetialActivity
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import java.io.Serializable

class NoticeListAdapter(context: Context) : ListBaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_notice, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Map<String, Any> = mDataList[position]
        val viewHolder = holder as ViewHolder
        //        String kind = item.get("kind")+"";
//        if (kind.equals("0")){
//            viewHolder.titleTv.setBackgroundResource(R.drawable.background_corners_gray_lightest);
//        }
        viewHolder.titleTv.text = item["title"].toString() + ""
        //viewHolder.mStatusTv.setText(item.get("abstract")+"");
/*if ((item.get("amt")+"").contains("-")){
            viewHolder.mMoneyTv.setText(UtilTools.getMoney(item.get("amt")+""));
        }else {
            viewHolder.mMoneyTv.setText("+"+UtilTools.getMoney(item.get("amt")+""));
        }*/viewHolder.tradeLay.setOnClickListener {
            val intent = Intent(mContext, NoticeDetialActivity::class.java)
            intent.putExtra("DATA", mDataList[position] as Serializable)
            mContext!!.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.title_tv)
        lateinit var titleTv: TextView
        @BindView(R.id.trade_lay)
        lateinit var tradeLay: CardView

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}