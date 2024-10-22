package com.lairui.easy.ui.module5.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.imageload.GlideUtils

class BankCardSelectAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<BankCardSelectAdapter.ViewHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null
    var selectItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.bank_card_item_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datas!![position]
        val accid = item["card"]!!.toString() + ""
        var bankName = item["bank_name"]!!.toString() + ""
        //val logopath = item["logopath"]!!.toString() + ""
        /*if (UtilTools.empty(bankName)) {
            if (accsn == "D") {
                bankName = "交易账户"
            } else {
                bankName = ""
            }
        }*/
        //设置银行和后四位卡号
        //holder.mTextView.text = bankName + "(" + UtilTools.getIDCardXing(accid) + ")"
        holder.mTextView.text = bankName + "(" +accid + ")"
        //设置银行卡对应的Logo
        GlideUtils.loadImage(mContext, "", holder.mImageView, R.drawable.default_bank)

        holder.mLayout!!.setOnClickListener { v ->
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(v, position)
            }
            selectItem = position
            notifyDataSetChanged()
        }

        holder.mLayout!!.isSelected = position == selectItem
    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImageView: ImageView = itemView.findViewById(R.id.iv_bank_ico)
        var mTextView: TextView = itemView.findViewById(R.id.tv_bank_num)
        var mLayout: LinearLayout = itemView.findViewById(R.id.ll_layout)


    }
}
