package com.lairui.easy.ui.module3.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class SelectMoneyAdapter(private val mContext: Context, var datas: MutableList<MutableMap<String, Any>>?) : RecyclerView.Adapter<SelectMoneyAdapter.ViewHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null
    var selectItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_day_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datas!![position]
        holder.mItemInfoTv.text = item["multiple"]!!.toString() + "倍\n"+item["money"]!!.toString()+"元"

        /*   GlideApp.with(mContext)
                .load(logopath)
                .apply(new RequestOptions().placeholder(R.drawable.tip_orange).error(R.drawable.tip_orange))
                .into(holder.mImageView);*/

        holder.mItemInfoTv.setOnClickListener { v ->
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(v, position)
            }
            selectItem = position
            notifyDataSetChanged()
        }

        if (position == selectItem) {
            holder.mItemInfoTv.setBackgroundResource(R.drawable.selected_red)
        } else {
            holder.mItemInfoTv.setBackgroundResource(R.drawable.corners_gray2)
        }
    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.itemInfoTv)
        lateinit var mItemInfoTv: TextView
        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
