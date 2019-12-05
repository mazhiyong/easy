package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.tool.UtilTools

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class ShouldShouMoneyAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    var booleanList: List<MutableMap<String, Any>>? = null

    private var mOnMyItemClickListener: OnMyItemClickListener? = null

    fun setOnMyItemClickListener(onMyItemClickListener: OnMyItemClickListener) {
        mOnMyItemClickListener = onMyItemClickListener
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_should_shou_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.mCheckBox!!.isChecked = false
        for (map in booleanList!!) {
            val m = map["value"] as MutableMap<String, Any>?
            if (m === item) {
                val b = map["selected"] as Boolean
                if (b) {
                    viewHolder.mCheckBox!!.isChecked = true
                } else {
                    viewHolder.mCheckBox!!.isChecked = false
                }
            }
        }


        viewHolder.mFukuanfangTv!!.text = item["payfirmname"]!!.toString() + ""
        viewHolder.mMoneyTv!!.text = UtilTools.getRMBMoney(item["paymoney"]!!.toString() + "")
        val status = item["poolsta"]!!.toString() + ""
        when (status) {
            "0"//未入池
            -> viewHolder.mItemLay!!.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.wheat))
            "2"//已入池
            -> viewHolder.mItemLay!!.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.white))
        }

        viewHolder.mCheckBox!!.setOnClickListener { viewHolder.mItemLay!!.performClick() }




        viewHolder.mItemLay!!.setOnClickListener {
            for (map in booleanList!!) {
                val mSelectMap = map["value"] as MutableMap<String, Any>?
                if (mSelectMap === item) {
                    val b = map["selected"] as Boolean
                    if (b) {
                        map.put("selected", false)
                    } else {
                        map.put("selected", true)
                    }
                }
            }

            if (mOnMyItemClickListener != null) {
                mOnMyItemClickListener!!.OnMyItemClickListener(viewHolder.mCheckBox!!, position)
            }
        }
    }


    fun cancelSelectAll() {
        for (map in booleanList!!) {
            map.put("selected", false)
        }

        notifyDataSetChanged()
    }

    fun selectAll() {
        for (map in booleanList!!) {
            map.put("selected", true)
        }

        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.fukuanfang_tv)
        lateinit var mFukuanfangTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView
        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox


        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
