package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.tool.UtilTools

import butterknife.BindView
import butterknife.ButterKnife

class SelectBankAdapter(context: Context) : ListBaseAdapter() {


    private lateinit var mBooleanList: MutableList<Boolean>
    private val mLayoutInflater: LayoutInflater

    lateinit var onMyItemClickListener: OnMyItemClickListener


    var booleanList: MutableList<Boolean>?
        get() = mBooleanList
        set(booleanList) {
            mBooleanList = booleanList!!
        }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder
        val b = mBooleanList!![position]
        if (b) {
            viewHolder.mCheckBox!!.isChecked = true
        } else {
            viewHolder.mCheckBox!!.isChecked = false
        }


        viewHolder.mBankCardTv!!.text = item["crdno"]!!.toString() + ""
        viewHolder.mBankCardTv!!.text = UtilTools.getIDXing(item["crdno"]!!.toString() + "")

        viewHolder.mCheckBox!!.setOnClickListener { viewHolder.mContentLay!!.performClick() }

        viewHolder.mContentLay!!.setOnClickListener {
            for (i in mBooleanList!!.indices) {//全部设为未选中
                val isChecked = mBooleanList!![i]
                if (i == position) {
                    mBooleanList!![position] = !isChecked
                } else {
                    mBooleanList!![i] = false
                }
            }
            if (onMyItemClickListener != null) {
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_select_bank, parent, false))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox
        @BindView(R.id.uesr_name_tv)
        lateinit var mUserNameTv: TextView
        @BindView(R.id.bank_name_tv)
        lateinit var mBankNameTv: TextView
        @BindView(R.id.bank_card_tv)
        lateinit var mBankCardTv: TextView
        @BindView(R.id.content_lay)
        lateinit var mContentLay: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }


}
