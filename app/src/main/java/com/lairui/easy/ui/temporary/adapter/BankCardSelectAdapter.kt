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
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife

class BankCardSelectAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<BankCardSelectAdapter.ViewHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null
    var selectItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.bank_card_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datas!![position]
        val accid = item["accid"]!!.toString() + ""
        val accsn = item["accsn"]!!.toString() + ""
        var bankName = item["opnbnknm"]!!.toString() + ""
        val logopath = item["logopath"]!!.toString() + ""
        if (UtilTools.empty(bankName)) {
            if (accsn == "D") {
                bankName = "交易账户"
            } else {
                bankName = ""
            }
        }
        //设置银行和后四位卡号
        holder.mTextView!!.text = bankName + "(" + UtilTools.getIDCardXing(accid) + ")"
        //设置银行卡对应的Logo
        GlideUtils.loadImage(mContext, logopath, holder.mImageView!!, R.drawable.default_bank)

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
