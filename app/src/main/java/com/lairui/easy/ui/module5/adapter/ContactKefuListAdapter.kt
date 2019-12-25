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

class ContactKefuListAdapter(context: Context) : ListBaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_contact_kefu, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Map<String, Any> = mDataList[position]
        val viewHolder = holder as ViewHolder
        //        String kind = item.get("kind")+"";
//        if (kind.equals("0")){
//            viewHolder.titleTv.setBackgroundResource(R.drawable.background_corners_gray_lightest);
//        }
        viewHolder.titleTv.text = item["type_name"].toString()
        when(item["type"].toString()){
            "1" ->{

                viewHolder.typeIv.setBackgroundResource(R.drawable.qq)
            }
            "2" ->{
                viewHolder.typeIv.setBackgroundResource(R.drawable.weichat)
            }
            "3" ->{
                viewHolder.typeIv.setBackgroundResource(R.drawable.phone)
            }

        }
        viewHolder.numberTv.text = item["number"].toString()

        viewHolder.tradeLay.setOnClickListener {



        }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var titleTv: TextView = itemView!!.findViewById(R.id.titleTv)
        var numberTv: TextView = itemView!!.findViewById(R.id.numberTv)
        var typeIv: ImageView = itemView!!.findViewById(R.id.typeIv)
        var tradeLay: LinearLayout = itemView!!.findViewById(R.id.lay)


    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}