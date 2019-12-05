package com.lairui.easy.ui.temporary.adapter

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

class CommonSelectAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<CommonSelectAdapter.ViewHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null
    var selectItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.common_dialog_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datas!![position]
        holder.mTextView!!.text = item["payfirmname"]!!.toString() + ""
        //设置银行卡对应的Logo
        /*   GlideApp.with(mContext)
                .load(logopath)
                .apply(new RequestOptions().placeholder(R.drawable.tip_orange).error(R.drawable.tip_orange))
                .into(holder.mImageView);*/

        holder.mLayout!!.setOnClickListener { v ->
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(v, position)
            }
            selectItem = position
            notifyDataSetChanged()
        }

        if (position == selectItem) {
            holder.mLayout!!.isSelected = true
        } else {
            holder.mLayout!!.isSelected = false
        }
    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.iv_bank_ico)
        lateinit var mImageView: ImageView
        @BindView(R.id.tv_bank_num)
        lateinit var mTextView: TextView
        @BindView(R.id.ll_layout)
        lateinit var mLayout: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
