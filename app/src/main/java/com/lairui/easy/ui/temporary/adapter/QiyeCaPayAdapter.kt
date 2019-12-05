package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BankOpenActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife

class QiyeCaPayAdapter(private val mContext: Context, private val mList: List<MutableMap<String, Any>>) : RecyclerView.Adapter<QiyeCaPayAdapter.ViewHodler>() {

    var itemClickListener: OnMyItemClickListener? = null

    private var mSelectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_chose_payway, parent, false)
        return ViewHodler(view)
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        val item = mList[position]

        val type = item["type"]!!.toString() + ""

        holder.mCheckBox!!.isChecked = false
        holder.mTvTitle!!.text = item["opnbnknm"]!!.toString() + ""
        if (type == "100") {
            GlideUtils.loadImage(mContext, "http://img.mp.sohu.com/upload/20170706/eba483641770409fbba6752a1c1c28c6_th.png", holder.mIvIcon!!)
        } else {
            holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
        }

        when (type) {
            "11" -> {
                holder.mMoneyTv!!.text = UtilTools.getRMBMoney(0.toString() + "")
                holder.mTypeTv!!.text = ""
                holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
            }
            "12" -> {
                holder.mMoneyTv!!.text = ""
                holder.mTypeTv!!.text = ""
                holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
            }
            "13" -> {
                holder.mMoneyTv!!.text = ""
                holder.mTypeTv!!.text = ""
                holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
            }
            "14" -> {
                holder.mMoneyTv!!.text = ""
                holder.mTypeTv!!.text = ""
                holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
            }
            "15" -> {
                holder.mMoneyTv!!.text = ""
                holder.mTypeTv!!.text = ""
                holder.mIvIcon!!.setImageResource((item["icon"] as Int?)!!)
            }
            "100" -> {
                holder.mMoneyTv!!.text = ""
                holder.mTypeTv!!.text = "快捷卡"
                GlideUtils.loadImage(mContext, "http://img.mp.sohu.com/upload/20170706/eba483641770409fbba6752a1c1c28c6_th.png", holder.mIvIcon!!)
            }
        }

        if (mSelectPosition == position) {
            holder.mCheckBox!!.isChecked = true
        } else {
            holder.mCheckBox!!.isChecked = false
        }

        holder.mMoreBankcardLayout!!.visibility = View.GONE


        holder.mCheckBox!!.setOnClickListener { holder.mLlLay!!.performClick() }
        holder.mLlLay!!.setOnClickListener {
            mSelectPosition = position
            if (itemClickListener != null) {
                itemClickListener!!.OnMyItemClickListener(holder.mCheckBox!!, position)
            }
            notifyDataSetChanged()
        }
        //选择更多客户
        holder.mMoreBankcardLayout!!.setOnClickListener {
            val intent = Intent(mContext, BankOpenActivity::class.java)
            intent.putExtra("backtype", "40")
            mContext.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.iv_icon)
        lateinit var mIvIcon: ImageView
        @BindView(R.id.tv_title)
        lateinit var mTvTitle: TextView
        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox
        @BindView(R.id.ll_lay)
        lateinit var mLlLay: LinearLayout
        @BindView(R.id.type_tv)
        lateinit var mTypeTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.ll_more_bankcard)
        lateinit var mMoreBankcardLayout: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
